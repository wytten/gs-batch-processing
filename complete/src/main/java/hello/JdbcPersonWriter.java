package hello;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class JdbcPersonWriter extends JdbcBatchItemWriter<Person> {

  protected static final Log logger = LogFactory.getLog(JdbcPersonWriter.class);

  protected JdbcOperations jdbcTemplate;

  protected static final String UPDATE = "update %s set %s where %s";

  protected static final String ASSIGN = "%s='%s'";

  protected static final String WHERE = "%s=%d";

  @Override
  public void write(List<? extends Person> items) throws Exception {

    if (!items.isEmpty()) {

      if (logger.isInfoEnabled()) {
        logger.info("Executing batch with " + items.size() + " items.");
      }

      long start = System.currentTimeMillis();
      String updateStatements[] = new String[items.size()];
      int q = 0;
      for (Person person : items) {
        String tableName = person.getTableName();
        String primaryKeyColumn = person.getPrimaryKeyColumn();
        Long primaryKeyValue = person.getPrimaryKeyValue();
        String where = String.format(WHERE, primaryKeyColumn, primaryKeyValue);
        List<String> assigns = new ArrayList<String>();
        for (String columnName : person.keySet()) {
          String value = (String) person.get(columnName);
          assigns.add(String.format(ASSIGN, columnName, value));
        }
        String allAssigns = StringUtils.collectionToCommaDelimitedString(assigns);
        String updateStatement = String.format(UPDATE, tableName, allAssigns, where);
        logger.info(updateStatement);
        updateStatements[q++] = updateStatement;
      }
      int results[] = jdbcTemplate.batchUpdate(updateStatements);
      int totalRows = 0;
      for (int i = 0; i < results.length; i++) {
        totalRows += results[i];
      }
      long seconds = (System.currentTimeMillis() - start) / 1000;
      float rate = (float) totalRows / (float) seconds;
      if (logger.isInfoEnabled()) {
        logger.info(String.format("Updated %d rows of %d attempts in %d seconds (%f/sec)", totalRows, items.size(), seconds, rate));
      }

    }
  }

  @Override
  public void afterPropertiesSet() {
    Assert.notNull(jdbcTemplate, "A DataSource or a NamedParameterJdbcTemplate is required.");
  }

  /**
   * Public setter for the data source for injection purposes.
   *
   * @param dataSource {@link javax.sql.DataSource} to use for querying against
   */
  public void setDataSource(DataSource dataSource) {
    if (jdbcTemplate == null) {
      this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
  }

}
