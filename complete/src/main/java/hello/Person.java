package hello;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Person implements Map<String, Object> {
    private Map<String, Object> map = new HashMap<String, Object>();

    @Override
    public int size() {
      return map.size();
    }

    @Override
    public boolean isEmpty() {
      return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
      return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
      return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
      return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
      return map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
      return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
      map.putAll(m);
    }

    @Override
    public void clear() {
      map.clear();
    }

    @Override
    public Set<String> keySet() {
      return map.keySet();
    }

    @Override
    public Collection<Object> values() {
      return map.values();
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
      return map.entrySet();
    }
    
    public String toString() {
      return String.format("%d %s %s", get("primaryKeyValue"), get("firstName"), get("lastName"));
    }
}
