import com.xtuer.bean.Role;
import com.xtuer.bean.User;
import com.xtuer.bean.audit.AuditConfig;
import com.xtuer.bean.audit.AuditConfigStep;
import com.xtuer.bean.audit.AuditType;
import org.junit.jupiter.api.Test;

public class AuditTest {
    /**
     * 测试审批配置的 JSON 序列化和反序列化
     */
    @Test
    public void testAuditConfigJson() {
        AuditConfigStep step = new AuditConfigStep();
        step.setStep(1);
        step.getAuditors().add(new User("Alice", "Passw0rd", Role.ROLE_ADMIN_SYSTEM));

        AuditConfig config = new AuditConfig();
        config.setType(AuditType.ORDER);
        config.getSteps().add(step);

        String json = config.getContentJson();
        System.out.println(json);

        AuditConfig temp = new AuditConfig();
        temp.setContentJson(json);
        System.out.println(temp.getContentJson());
    }
}
