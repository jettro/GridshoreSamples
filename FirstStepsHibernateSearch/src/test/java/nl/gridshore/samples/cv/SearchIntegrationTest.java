package nl.gridshore.samples.cv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Jettro Coenradie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-config.xml"})
public class SearchIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PersonRepository personRepository;

    @Test
    @Transactional
    public void useIndex() {
        List<Person> results = personRepository.searchFor("development");
        assertEquals("The amount of found persons in the full text search is not correct", 1, results.size());
        for (Person person : results) {
            System.out.println("Person title : " + person.getTitle());
        }

    }
}
