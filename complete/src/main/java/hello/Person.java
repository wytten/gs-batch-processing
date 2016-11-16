package hello;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Person implements Map<String, String> {
    private Map<String, String> map = new HashMap<String, String>();

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
    public String get(Object key) {
      return map.get(key);
    }

    @Override
    public String put(String key, String value) {
      return map.put(key, value);
    }

    @Override
    public String remove(Object key) {
      return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
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
    public Collection<String> values() {
      return map.values();
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
      return map.entrySet();
    }
    
    public String toString() {
      return String.format("%s %s", get("firstName"), get("lastName"));
    }
}
