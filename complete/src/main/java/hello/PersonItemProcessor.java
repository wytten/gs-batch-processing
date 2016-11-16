package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

  private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);
  
  @Override
  public Person process(final Person person) throws Exception {
    final Person transformedPerson = new Person();
    for (String key : person.keySet()) {
      transformedPerson.put(key, person.get(key).toUpperCase());
    }

    log.info("Converting (" + person + ") into (" + transformedPerson + ")");

    return transformedPerson;
  }

}
