package sec.webshop.service;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sec.webshop.domain.Item;
import sec.webshop.domain.Order;
import sec.webshop.domain.OrderItem;
import sec.webshop.domain.ShoppingCart;

import sec.webshop.repository.ItemRepository;
import sec.webshop.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private ShoppingCart shoppingCart;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> list() {
        return orderRepository.findAll();
    }

    @Transactional
    public void placeOrder(String name, String address) {

        Order order = new Order();
        order.setName(name);
        order.setAddress(address);

        List<OrderItem> items = new ArrayList<>();
        for (Item item : shoppingCart.getItems().keySet()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(itemRepository.findOne(item.getId()));
            orderItem.setItemCount(shoppingCart.getItems().get(item));

            items.add(orderItem);
        }

        order.setOrderItems(items);

        orderRepository.save(order);
        shoppingCart.getItems().clear();
    }
}
