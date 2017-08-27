package filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Request {
    private String message;

    public Request(String message) {
        this.message = message;
    }
}
