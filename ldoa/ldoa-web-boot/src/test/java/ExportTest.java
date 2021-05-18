import com.xtuer.Application;
import com.xtuer.bean.product.ProductFilter;
import com.xtuer.bean.sales.CustomerFilter;
import com.xtuer.service.CustomerService;
import com.xtuer.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest(classes = { Application.class })
public class ExportTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private CustomerService customerService;

    // 导出产品
    @Test
    public void exportProducts() throws IOException {
        String url = productService.exportProducts(new ProductFilter());
        System.out.println(url);
    }

    // 导出客户
    @Test
    public void exportCustomers() throws IOException {
        String url = customerService.exportCustomers(new CustomerFilter());
        System.out.println(url);
    }
}
