import com.xtuer.util.CommonUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

public class TestUuid {
    @Test
    public void testUuid() {
        String uuid1 = CommonUtils.uuid();
        String uuid2 = CommonUtils.uuid();

        System.out.println(uuid1);
        System.out.println(uuid2);

        Assert.assertNotEquals(uuid1, uuid2);
    }

    @Test
    public void testDirectoryNameByUuid() {
        System.out.println("12341adfsdf".replaceAll("^(\\w+)(\\..*)", "$1"));
        System.out.println("12341adfsdf.doc".replaceAll("^(\\w+)(\\..*)", "$1"));

        Map<Integer, Integer> counts = new TreeMap<>();
        for (int i=0; i<100; ++i) {
            counts.put(i, 0);
        }

        for (int i=0; i<100000; ++i) {
            String uuid = CommonUtils.uuid();
            int n = Integer.parseInt(CommonUtils.directoryNameByUuid(uuid));
            int sum = counts.get(n) + 1;
            counts.put(n, sum);
        }

        System.out.println(counts); // 结果基本是平均分布
    }
}
