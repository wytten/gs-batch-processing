package hello;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcUtils;

import oracle.jdbc.pool.OracleDataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<Person> old_reader() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
        reader.setResource(new ClassPathResource("sample-data.csv"));
        reader.setLineMapper(new DefaultLineMapper<Person>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "firstName", "lastName" });
            }});
            setFieldSetMapper(new PersonFieldSetMapper());
        }});
        return reader;
    }
    
  private DataSource oracle_datasource() {
    OracleDataSource dataSource = null;
    Properties secrets = new Properties();
    try {
      dataSource = new OracleDataSource();
      secrets.load(new FileInputStream("secrets.properties"));
    } catch (Exception e) {
      throw new Error(e);
    }
    dataSource.setUser(secrets.getProperty("oracle.database.user"));
    dataSource.setPassword(secrets.getProperty("oracle.database.password"));
    dataSource.setURL(secrets.getProperty("oracle.database.url"));
    // dataSource.setImplicitCachingEnabled(true);
    // dataSource.setFastConnectionFailoverEnabled(true);
    return dataSource;
  }

    // tag::readerwriterprocessor[]
    @Bean
    public JdbcCursorItemReader<Person> reader() {
      JdbcCursorItemReader<Person> reader = new JdbcCursorItemReader<Person>();
      reader.setDataSource(oracle_datasource());
      reader.setSql("select eob_id, patient_first_name, patient_last_name from sot22.drw_pay_eob");
      reader.setRowMapper(new RowMapper<Person>() {

        @Override
        public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
          Person person = new Person();
          person.put("primaryKeyColumn", "eob_id");
          person.put("primaryKeyValue", rs.getLong(1));
          person.put("firstName", rs.getString(2));
          person.put("lastName", rs.getString(3));
          return person;
        }
        
      });
      return reader;
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> old_writer() {
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
        writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
    }
    // end::readerwriterprocessor[]

    @Bean
    public JdbcBatchItemWriter<Person> writer() {
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
        // TODO: There's a problem here, :primaryKeyColumn doesn't work so I had to hardcode eob_id
        writer.setSql("update sot22.drw_pay_eob set patient_first_name=:firstName, patient_last_name=:lastName where eob_id=:primaryKeyValue");
        writer.setDataSource(oracle_datasource());
        writer.setItemSqlParameterSourceProvider(new ItemSqlParameterSourceProvider<Person>() {
          
          @Override
          public SqlParameterSource createSqlParameterSource(Person item) {
            return new SqlParameterSource() {
              
              @Override
              public boolean hasValue(String paramName) {
                return item.get(paramName) != null;
              }
              
              @Override
              public Object getValue(String paramName) throws IllegalArgumentException {
                return item.get(paramName);
              }
              
              @Override
              public String getTypeName(String paramName) {
                return null;
              }
              
              @Override
              public int getSqlType(String paramName) {
                return JdbcUtils.TYPE_UNKNOWN;
              }
            };
          }
        });
        return writer;
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(1000)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    // end::jobstep[]
}
