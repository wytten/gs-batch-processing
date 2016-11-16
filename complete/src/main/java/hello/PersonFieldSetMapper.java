package hello;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class PersonFieldSetMapper implements FieldSetMapper<Person> {

  @Override
  public Person mapFieldSet(FieldSet fieldSet) throws BindException {
    Person person = new Person();
    for (String name : fieldSet.getNames()) {
      person.put(name, fieldSet.readString(name));
    }
    return person;
  }
}
