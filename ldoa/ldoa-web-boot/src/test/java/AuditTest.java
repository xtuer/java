import com.xtuer.bean.audit.Audit;
import com.xtuer.bean.audit.AuditType;
import org.junit.jupiter.api.Test;

public class AuditTest {
    @Test
    public void testAuditJson() {
        Audit audit = new Audit();
        audit.setAuditId(1L)
                .setType(AuditType.ORDER)
                .setPassed(true);
        String json = audit.getContentJson();
        System.out.println(json);

        Audit temp = new Audit();
        temp.setContentJson(json);
        System.out.println(temp.getContentJson());
    }
}
