package sec.webshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sec.webshop.domain.ShoppingCart;
import sec.webshop.repository.ItemRepository;

@Controller
public class CartController {

    @Autowired
    private ShoppingCart shoppingCart;

    @Autowired
    private ItemRepository itemRepository;

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("items", shoppingCart.getItems());
        model.addAttribute("sum", shoppingCart.getSum());
        return "cart";
    }

    @RequestMapping(value = "/cart/items/{itemId}", method = RequestMethod.POST)
    public String add(@PathVariable Long itemId) {
        shoppingCart.addToCart(itemRepository.findOne(itemId));
        return "redirect:/cart";
    }

    @RequestMapping(value = "/cart/items/{itemId}", method = RequestMethod.DELETE)
    public String remove(@PathVariable Long itemId) {
        shoppingCart.removeFromCart(itemRepository.findOne(itemId));
        return "redirect:/cart";
    }
}
