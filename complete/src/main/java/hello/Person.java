package hello;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

public class Person {
    private Map<String, Object> map = new HashMap<String, Object>();
    private String tableName;
    private String primaryKeyColumn;
    private Long primaryKeyValue;

    public Person() {
    }

    public Person(Person person) {
      this.tableName = person.tableName;
      this.primaryKeyColumn = person.primaryKeyColumn;
      this.primaryKeyValue = person.primaryKeyValue;
    }

    public int size() {
      return map.size();
    }


    public boolean isEmpty() {
      return map.isEmpty();
    }


    public boolean containsKey(Object key) {
      return map.containsKey(key);
    }


    public boolean containsValue(Object value) {
      return map.containsValue(value);
    }


    public Object get(Object key) {
      return map.get(key);
    }


    public Object put(String key, Object value) {
      return map.put(key, value);
    }


    public Object remove(Object key) {
      return map.remove(key);
    }


    public void putAll(Map<? extends String, ? extends Object> m) {
      map.putAll(m);
    }


    public void clear() {
      map.clear();
    }


    public Set<String> keySet() {
      return map.keySet();
    }


    public Collection<Object> values() {
      return map.values();
    }


    public Set<java.util.Map.Entry<String, Object>> entrySet() {
      return map.entrySet();
    }
    
    public String toString() {
      List<String> columnValues = new ArrayList<String>();
      for (String columnName : keySet()) {
        String value = (String) get(columnName);
        columnValues.add(String.format("%s='%s'", columnName, value));
      }
      String allColumnValues = StringUtils.collectionToCommaDelimitedString(columnValues);
      return String.format("%d %s", get("primaryKeyValue"), allColumnValues);
    }


    public String getTableName() {
      return tableName;
    }


    public String getPrimaryKeyColumn() {
      return primaryKeyColumn;
    }


    public Long getPrimaryKeyValue() {
      return primaryKeyValue;
    }


    public void setTableName(String tableName) {
      this.tableName = tableName;
    }


    public void setPrimaryKeyColumn(String primaryKeyColumn) {
      this.primaryKeyColumn = primaryKeyColumn;
    }


    public void setPrimaryKeyValue(Long primaryKeyValue) {
      this.primaryKeyValue = primaryKeyValue;
    }
}
