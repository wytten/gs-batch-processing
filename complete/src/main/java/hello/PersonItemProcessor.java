package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

  private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);
  
  @Override
  public Person process(final Person person) throws Exception {
    final Person transformedPerson = new Person(person);
    for (String key : person.keySet()) {
      Object value = person.get(key);
      if (value instanceof String) {
        // TODO: Don't remove apostrophes
        value = ((String) value).toUpperCase().replaceAll("'", "");
      } else if (value instanceof Long) {
        value = (Long) value;
      } else {
        throw new IllegalStateException(""+value);
      }
      transformedPerson.put(key, value);
    }

    log.debug("Converting (" + person + ") into (" + transformedPerson + ")");

    return transformedPerson;
  }

}
