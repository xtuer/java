import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.xtuer.Application;
import com.xtuer.bean.sales.Customer;
import com.xtuer.service.CustomerService;
import com.xtuer.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = { Application.class })
public class PoiTest {
    private static final File CUSTOMER_EXCEL = new File("D:/workspace/Java/ldoa/ldoa-web-boot/doc/客户列表.xls");

    @Autowired
    private CustomerService customerService;

    /**
     * 从 Excel 中导入客户信息
     */
    @Test
    public void importCustomer() {
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);

        List<Customer> customers = ExcelImportUtil.importExcel(CUSTOMER_EXCEL, Customer.class, params);
        customers = customers.stream().filter(c -> StringUtils.isNotBlank(c.getCustomerSn())).collect(Collectors.toList());
        Utils.dump(customers);
    }

    /**
     * 从 Excel 中导入客户信息保存到数据库
     */
    @Test
    public void importCustomerIntoDb() {
        customerService.importCustomers(CUSTOMER_EXCEL);
    }
}
