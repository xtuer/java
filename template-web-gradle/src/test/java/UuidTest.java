import com.xtuer.util.CommonUtils;
import org.junit.Assert;
import org.junit.Test;

public class UuidTest {
    @Test
    public void testUuid() {
        String uuid1 = CommonUtils.uuid();
        String uuid2 = CommonUtils.uuid();

        System.out.println(uuid1);

        Assert.assertNotEquals(uuid1, uuid2);
    }
}
