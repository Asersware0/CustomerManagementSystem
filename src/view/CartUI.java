package view;

import businness.BasketController;
import businness.CartController;
import businness.ProductController;
import core.Helper;
import entity.Basket;
import entity.Cart;
import entity.Customer;
import entity.Product;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;

public class CartUI extends JFrame {
    private JPanel container;
    private JLabel lbl_customer_name;
    private JTextField fld_cart_date;
    private JTextArea tarea_cart_note;
    private JButton btn_cart;
    private JLabel lbl_title;
    private Customer customer;
    private BasketController basketController;
    private CartController cartController;
    private ProductController productController;

    public CartUI(Customer customer) {
        this.customer = customer;
        this.basketController = new BasketController();
        this.cartController = new CartController();
        this.productController = new ProductController();

        this.add(container);
        this.setTitle("Sipariş Oluştur");
        this.setSize(300,350);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2;
        this.setLocation(x,y);
        this.setVisible(true);

        if (customer.getId() == 0) {
            Helper.showMsg("Lütfen Geçerli Bir Müşteri Seçiniz !");
            dispose();
        }

        ArrayList<Basket> baskets = this.basketController.findAll();
        if (baskets.size() == 0) {
          Helper.showMsg("Lütfen Sepete Ürün Ekleyiniz !");
          dispose();
        }

        this.lbl_customer_name.setText("Müşteri: " + this.customer.getName());

        btn_cart.addActionListener(e -> {
         if(Helper.isFieldEmpty(fld_cart_date)) {
             Helper.showMsg("fill");
         }else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                 for (Basket basket : baskets) {
                    if (basket.getProduct().getStock() <= 0) continue;

                    Cart cart = new Cart();
                    cart.setCustomerId(this.customer.getId());
                    cart.setProduct(basket.getProduct());
                    cart.setProductId(basket.getProduct().getId());
                    cart.setDate(LocalDate.parse(fld_cart_date.getText(), formatter));
                    cart.setDate(LocalDate.now());
                    cart.setPrice(basket.getProduct().getPrice());
                    cart.setNote(this.tarea_cart_note.getText());
                    this.cartController.save(cart);

                    Product unStockProduct = basket.getProduct();
                    unStockProduct.setStock(unStockProduct.getStock() - 1);
                    this.productController.update(unStockProduct);
                }

                this.basketController.clear();
                Helper.showMsg("done");
                dispose();
             }


        });
    }



    //hoca ile yapılan çalışmadı burda kalsın
    /*private void createUIComponents() {
        this.fld_cart_date = new JFormattedTextField(new SimpleDateFormat("##/##/####"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.fld_cart_date.setText(formatter.format(LocalDate.now()));
        this.fld_cart_date.setEditable(false);
        }*/

    private void createUIComponents() {

        DefaultFormatter formatter = new DefaultFormatter();
        formatter.setCommitsOnValidEdit(false);

        this.fld_cart_date = new JFormattedTextField(formatter);
        this.fld_cart_date.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        this.fld_cart_date.setEditable(true); // kullanıcı yazamasın
    }


}

