package nl.gridshore.samples.cv;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jettro Coenradie
 */
@Repository
@Transactional
public class PersonRepository {
    private final static Logger logger = LoggerFactory.getLogger(PersonRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    public void store(Person person) {
//        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
//        fullTextEntityManager.persist(person);
        entityManager.persist(person);
    }

    public List<Person> searchFor(String chief) {
        logger.debug("Start searching for: {}", chief);
        String[] personFields = {"title", "elevatorPitch"};

        Map<String, Float> boostPerField = new HashMap<String, Float>(2);
        boostPerField.put("title", (float) 4);
        boostPerField.put("elevatorPitch", (float) 1);

        QueryParser parser = new MultiFieldQueryParser(
                Version.LUCENE_29,
                personFields,
                new StandardAnalyzer(Version.LUCENE_29),
                boostPerField
        );

        org.apache.lucene.search.Query luceneQuery;
        try {
            luceneQuery = parser.parse(chief);
        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse query: " + chief, e);
        }

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Person.class);
        fullTextQuery.setMaxResults(20);

        logger.debug("End searching for chief: {}", chief);
        return fullTextQuery.getResultList();
    }
}
