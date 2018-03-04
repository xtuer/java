package bean;

import lombok.ToString;
import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Person {
    @Id
    private Long id;
    private String firstName;
    private String lastName;

    public Person() {

    }

    public Person(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
