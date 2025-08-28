package businness;

import dao.BasketDao;
import entity.Basket;
import entity.Customer;

import java.util.ArrayList;

public class BasketController {

    private BasketDao basketDao = new BasketDao();
    public ArrayList<Basket> findAll() {
        return this.basketDao.findAll();
    }
    public boolean save(Basket basket) {
        return basketDao.save(basket);
    }

    public boolean clear () {
        return basketDao.clear();
    }
}
