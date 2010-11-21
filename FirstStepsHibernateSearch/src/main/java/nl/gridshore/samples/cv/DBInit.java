package nl.gridshore.samples.cv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Jettro Coenradie
 */
@Component
public class DBInit {

    @Autowired
    private PersonRepository personRepository;

    @PostConstruct
    @Transactional
    public void initializeWithData() {
        Person person = new Person("Chief Architect", "I am the chief architect of the best java development team.");
        personRepository.store(person);
    }
}
