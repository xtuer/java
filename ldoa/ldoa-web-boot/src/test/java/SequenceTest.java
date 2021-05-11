import com.xtuer.Application;
import com.xtuer.service.CommonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { Application.class })
public class SequenceTest {
    @Autowired
    private CommonService commonService;

    @Test
    public void testNextSequence() {
        int sn = commonService.nextSequence("Flash-2021");
        System.out.println(sn);
    }
}
