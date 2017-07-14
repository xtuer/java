import com.xtuer.service.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/spring-beans.xml"})
public class TransactionTest {
    @Autowired
    private TransactionService service;

    @Test
    public void testTransaction() {
        service.updateData();
    }
}
