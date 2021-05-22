import com.xtuer.Application;
import com.xtuer.bean.order.MaintenanceOrderFilter;
import com.xtuer.bean.order.OrderFilter;
import com.xtuer.bean.product.ProductFilter;
import com.xtuer.bean.sales.CustomerFilter;
import com.xtuer.bean.sales.SalesOrderFilter;
import com.xtuer.service.*;
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

    @Autowired
    private MaintenanceOrderService maintenanceOrderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SalesOrderService salesOrderService;

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

    // 导出维保订单
    @Test
    public void exportMaintenanceOrders() throws IOException {
        String url = maintenanceOrderService.exportMaintenanceOrders(new MaintenanceOrderFilter());
        System.out.println(url);
    }

    // 导出生产订单
    @Test
    public void exportOrders() throws IOException {
        OrderFilter filter = new OrderFilter();
        filter.setState(-1);
        String url = orderService.exportOrders(filter);
        System.out.println(url);
    }

    // 导出销售订单
    @Test
    public void exportSalesOrders() throws IOException {
        SalesOrderFilter filter = new SalesOrderFilter();
        filter.setState(-1);
        String url = salesOrderService.exportSalesOrders(filter);
        System.out.println(url);
    }

    // 导出支付信息的销售订单
    @Test
    public void exportSalesOrdersForPayment() throws IOException {
        SalesOrderFilter filter = new SalesOrderFilter();
        filter.setSearchType(SalesOrderFilter.SEARCH_TYPE_SHOULD_PAY);
        String url = salesOrderService.exportSalesOrdersForPayment(filter);
        System.out.println(url);
    }
}
