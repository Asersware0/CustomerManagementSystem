package businness;

import core.Helper;
import core.Item;
import dao.CustomerDao;
import dao.ProductDao;
import entity.Customer;
import entity.Product;

import java.util.ArrayList;

public class ProductController {
    private final ProductDao productDao = new ProductDao();

    public ArrayList<Product> findAll() {
        return this.productDao.findAll();
    }
    public Product getById(int id) {
        return this.productDao.getById(id);
    }

    public  boolean save(Product product){
        return this.productDao.save(product);
    }

    public boolean update (Product product) {
        if (this.getById(product.getId()) == null) {
            Helper.showMsg(product.getId() + "ID kayıtlı ürün bulunamadı");
            return false;
        }
        return this.productDao.update(product);
    }

    public boolean delete (int id) {
        if (this.getById(id) == null) {
            Helper.showMsg(id + "ID kayıtlı ürün bulunamadı");
            return false;
        }
        return this.productDao.delete(id);
    }
    /*
    public ArrayList<Product> filter (String name, String code, Item isStock) {
        String query = "SELECT * FROM product";

        ArrayList<String> whereList = new ArrayList<>();

        if (name.length() > 0) {
            whereList.add("name LIKE '%" + name + "%'");
        }

        if (code.length() > 0) {
            whereList.add("code LIKE '%" + code + "%'");
        }

        if (isStock != null) {
            if(isStock.getKey() == 1) {
                whereList.add("stock > 0");
            }else{
                whereList.add("stock < 0");
            }
        }

        if (whereList.size() > 0) {
            String whereQuerry = String.join(" AND ", whereList);
            query +=  " WHERE " + whereQuerry;
        }
        return this.productDao.query(query);
    }
    */
    public ArrayList<Product> filter(String name, String code, Item isStock) {
        String query = "SELECT * FROM product";

        ArrayList<String> whereList = new ArrayList<>();

        // name kontrolü
        if (name != null && !name.isEmpty()) {
            whereList.add("name LIKE '%" + name + "%'");
        }

        // code kontrolü
        if (code != null && !code.isEmpty()) {
            whereList.add("code LIKE '%" + code + "%'");
        }

        // stok durumu kontrolü
        if (isStock != null) {
            if (isStock.getKey() == 1) {
                whereList.add("stock > 0");
            } else {
                whereList.add("stock = 0");
            }
        }

        // where ekleme
        if (!whereList.isEmpty()) {
            String whereQuery = String.join(" AND ", whereList);
            query += " WHERE " + whereQuery;
        }

        return this.productDao.query(query);
    }

}
