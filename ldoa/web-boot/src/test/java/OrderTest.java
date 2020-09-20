import com.xtuer.Application;
import com.xtuer.bean.Order;
import com.xtuer.bean.OrderItem;
import com.xtuer.mapper.OrderMapper;
import com.xtuer.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest(classes = { Application.class })
public class OrderTest {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;

    @Test
    public void insertOrder() {
        OrderItem item1 = new OrderItem();
        item1.setOrderItemId(1)
                .setOrderId(1)
                .setProductId(409255664418822L)
                .setCount(1);

        OrderItem item2 = new OrderItem();
        item2.setOrderItemId(2)
                .setOrderId(1)
                .setProductId(409255664418824L)
                .setCount(2);

        Order order = new Order();
        order.setOrderId(1)
                .setOrderSn("SN-1")
                .setCustomerCompany("Salmon")
                .setCustomerContact("Alice")
                .setCustomerAddress("北京")
                .setOrderDate(new Date())
                .setDeliveryDate(new Date())
                .setSalespersonId(1);

        order.getItems().add(item1);
        order.getItems().add(item2);

        orderService.upsertOrder(order);
    }
}
