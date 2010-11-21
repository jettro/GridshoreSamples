package nl.gridshore.samples.cv;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * @author Jettro Coenradie
 */
public class Runner {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:test-config.xml");
        PersonRepository personRepository = (PersonRepository) context.getBean("personRepository");
        personRepository.store(new Person("Chief of pain", "I am the chief of pain"));
        List<Person> persons = personRepository.searchFor("Chief");
        if (persons != null) {
            System.out.println("Number of search results : " + persons.size());
        } else {
            System.out.println("Oops, found nothing");
        }
    }
}
