package bootimport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomConfiguration {
    @Bean
    public Tom tom() {
        Tom tom = new Tom();
        tom.setName("Tom 1");
        return tom;
    }
}
