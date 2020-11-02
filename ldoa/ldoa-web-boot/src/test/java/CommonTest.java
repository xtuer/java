import com.xtuer.bean.audit.AuditItem;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class CommonTest {
    @Test
    public void testLambdaMax1() {
        List<AuditItem> items = prepareDate();
        int maxStep = items.stream().map(AuditItem::getStep).max(Comparator.comparing(Integer::valueOf)).orElse(0);
        Assert.isTrue(maxStep == 5, "");
    }

    @Test
    public void testLambdaMax2() {
        List<AuditItem> items = prepareDate();
        int maxStep = items.stream().mapToInt(AuditItem::getStep).max().orElse(0);
        Assert.isTrue(maxStep == 5, "");
    }

    private List<AuditItem> prepareDate() {
        List<AuditItem> items = new LinkedList<>();
        items.add(new AuditItem().setStep(1));
        items.add(new AuditItem().setStep(2));
        items.add(new AuditItem().setStep(3));
        items.add(new AuditItem().setStep(4));
        items.add(new AuditItem().setStep(5));

        return items;
    }
}
