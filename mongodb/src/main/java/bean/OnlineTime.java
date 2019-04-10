package bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OnlineTime {
    @Id String id;
    String date;
    int hour;
    int total;

    public String getDate() {
        return date != null ? date : id;
    }
}
