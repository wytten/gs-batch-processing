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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

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
          person.setTableName("sot22.drw_pay_eob");
          person.setPrimaryKeyColumn("eob_id");
          person.setPrimaryKeyValue(rs.getLong(1));
          person.put("patient_first_name", rs.getString(2));
          person.put("patient_last_name", rs.getString(3));
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
    public JdbcPersonWriter writer() {
      JdbcPersonWriter writer = new JdbcPersonWriter();
      writer.setDataSource(oracle_datasource());
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
