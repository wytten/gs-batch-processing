package hello;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

  private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);
  
  private Map<String, String> map = new HashMap<String, String>();


  @Override
  public Person process(final Person person) throws Exception {
    final Person transformedPerson = new Person();
    for (String key : person.keySet()) {
      String value = map.get(person.get(key));
      if ( value == null ) {
        value = "UNKNOWN";
      }
      transformedPerson.put(key, value);
    }

    log.info("Converting (" + person + ") into (" + transformedPerson + ")");

    return transformedPerson;
  }

}
