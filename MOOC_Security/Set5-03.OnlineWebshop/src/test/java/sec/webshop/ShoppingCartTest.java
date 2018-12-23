package sec.webshop;

import java.util.Map;
import org.junit.Test;
import sec.webshop.domain.Item;
import static org.junit.Assert.*;
import sec.webshop.domain.ShoppingCart;

public class ShoppingCartTest {

    @Test
    public void canAddAnItemToCart() {
        Item i = new Item();
        i.setName("Porkala");
        i.setPrice(0.5);
        
        ShoppingCart cart = new ShoppingCart();
        cart.addToCart(i);
        
        Map<Item, Long> cartContents = cart.getItems();
        
        assertTrue("Item should be in the cart after it has been added to the cart.", cartContents.containsKey(i));
        assertEquals("When item has been added to cart once, it's count should be one.", new Long(1), cartContents.get(i));

        cart.addToCart(i);
        cart.addToCart(i);
        
        assertEquals("When an item has been added to the cart three times, the count for that specific item should be three.", new Long(3), cartContents.get(i));

        cart.removeFromCart(i);
        
        assertEquals("When an item has been added to cart three times and removed once, there should be two items remaining.", new Long(2), cartContents.get(i));
    }
}
