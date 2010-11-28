package nl.gridshore.samples.cv;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Jettro Coenradie
 */
@Entity
@Indexed
public class Person {
    @Id
    @GeneratedValue
    @DocumentId
    private Integer id;


    @Field
    private String title;

    @Field
    private String elevatorPitch;

    public Person(String title, String elevatorPitch) {
        this.elevatorPitch = elevatorPitch;
        this.title = title;
    }

    public Person() {
        // required by jpa
    }

    public String getElevatorPitch() {
        return elevatorPitch;
    }

    public void setElevatorPitch(String elevatorPitch) {
        this.elevatorPitch = elevatorPitch;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
