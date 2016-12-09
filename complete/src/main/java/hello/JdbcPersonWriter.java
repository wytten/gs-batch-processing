package hello;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class JdbcPersonWriter extends JdbcBatchItemWriter<Person> {

  protected static final Log logger = LogFactory.getLog(JdbcPersonWriter.class);

  protected NamedParameterJdbcOperations namedParameterJdbcTemplate;

  protected static final String UPDATE = "update %s set %s where %s";

  protected static final String ASSIGN = "%s=:%s";

  protected static final String WHERE = "rowid='%s'";

  @Override
  public void write(List<? extends Person> items) throws Exception {

    if (!items.isEmpty()) {

      if (logger.isInfoEnabled()) {
        logger.info("Executing batch with " + items.size() + " items.");
      }

      long start = System.currentTimeMillis();
      for (Person person : items) {
        // construct the list of sql assignments and build related params map
        List<String> assigns = new ArrayList<String>();
        int q = 0;
        Map<String, String> params = new HashMap<String, String>();;
        for (String columnName : person.keySet()) {
          String value = (String) person.get(columnName);
          String rhs = String.format("value%d", q++);
          params.put(rhs, value);
          assigns.add(String.format(ASSIGN, columnName, rhs));
        }
        
        // put the update statement together
        final String allAssigns = StringUtils.collectionToCommaDelimitedString(assigns);
        final String where = String.format(WHERE, person.getRowId());
        final String updateStatement = String.format(UPDATE, person.getTableName(), allAssigns, where);
        if (logger.isDebugEnabled()) {
          logger.debug(updateStatement);
        }
        
        @SuppressWarnings("unchecked")
        Map<String, String>[] batchValues = (Map<String, String>[]) new Map[] {params};
        namedParameterJdbcTemplate.batchUpdate(updateStatement, batchValues);
      }
      
      long seconds = (System.currentTimeMillis() - start) / 1000;
      float rate = (float) items.size() / (float) seconds;
      if (logger.isInfoEnabled()) {
        logger.info(String.format("Updated %d rows in %d seconds (%f/sec)", items.size(), seconds, rate));
      }

    }
  }

  @Override
  public void afterPropertiesSet() {
    Assert.notNull(namedParameterJdbcTemplate, "A DataSource or a NamedParameterJdbcTemplate is required.");
  }

  /**
   * Public setter for the data source for injection purposes.
   *
   * @param dataSource {@link javax.sql.DataSource} to use for querying against
   */
    public void setDataSource(DataSource dataSource) {
      if (namedParameterJdbcTemplate == null) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
      }
    }

  /**
   * Public setter for the {@link NamedParameterJdbcOperations}.
   * @param namedParameterJdbcTemplate the {@link NamedParameterJdbcOperations} to set
   */
  public void setJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }
}
