package view;

import businness.BasketController;
import businness.CartController;
import businness.CustomerController;
import businness.ProductController;
import core.Helper;
import core.Item;
import entity.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DashboardUI extends JFrame {

    private JPanel container;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JTabbedPane tab_menu;
    private JPanel pnl_customer;
    private JScrollPane scrl_customer;
    private JTable tbl_customer;
    private JPanel pnl_customer_filter;
    private JTextField fld_f_customer_name;
    private JComboBox<Customer.TYPE> cmb_fcustomer_type;
    private JButton btn_customer_filter;
    private JButton btn_customer_filter_reset;
    private JButton btn_customer_new;
    private JLabel lbl_f_customer_name;
    private JLabel lbl_f_customer_type;
    private JPanel pnl_product;
    private JScrollPane scrl_product;
    private JTable tbl_product;
    private JPanel pnl_product_filter;
    private JTextField fld_f_product_code;
    private JButton btn_product_filter;
    private JButton btn_product_filter_reset;
    private JButton btn_product_new;
    private JLabel lbl_f_product_name;
    private JTextField fld_f_product_name;
    private JLabel lbl_f_product_code;
    private JComboBox<Item> cmb_f_product_stock;
    private JLabel lbl_f_product_stock;
    private JPanel pnl_basket;
    private JPanel pnl_basket_top;
    private JScrollPane scrl_basket;
    private JComboBox<Item> cmb_basket_customer;
    private JButton btn_basket_reset;
    private JButton btn_basket_new;
    private JLabel lbl_basket_price;
    private JLabel lbl_basket_count;
    private JTable tbl_basket;
    private JScrollPane scrl_cart;
    private JTable tbl_cart;
    private User user;
    private CustomerController customerController;
    private ProductController productController;
    private BasketController basketController;
    private CartController cartController;
    private DefaultTableModel tmdl_customer = new DefaultTableModel();
    private DefaultTableModel tmdl_product = new DefaultTableModel();
    private DefaultTableModel tmdl_basket = new DefaultTableModel();
    private DefaultTableModel tmdl_cart = new DefaultTableModel();
    private JPopupMenu popup_customer = new JPopupMenu();
    private JPopupMenu popup_product = new JPopupMenu();



    public DashboardUI(User user) {
        this.user = user;
        this.customerController = new CustomerController();
        this.productController = new ProductController();
        this.basketController = new BasketController();
        this.cartController = new CartController();

        if(user == null){
            Helper.showMsg("error");
            dispose();
        }
        this.add(container);
        this.setTitle("Müşteri Yönetim Sistemi");
        this.setSize(1000,500);

        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2;

        this.setLocation(x,y);
        this.setVisible(true);

        this.lbl_welcome.setText("Hoşgeldiniz Sayın " + this. user.getName());
        this.btn_logout.addActionListener(e -> {
            dispose();
            LoginUI loginUI = new LoginUI();
        });

        //customer tab
        loadCustomerTable(null);
        loadCustomerPopupMenu();
        loadCustomerButtonEvent();
        this.cmb_fcustomer_type.setModel(new DefaultComboBoxModel<>(Customer.TYPE.values()));
        this.cmb_fcustomer_type.setSelectedItem(null);

        //product tab
        loadProductTable(null);
        loadProductPopupMenu();
        loadProductButtonEvent();
        this.cmb_f_product_stock.addItem(new Item(1, "Stokta Var"));
        this.cmb_f_product_stock.addItem(new Item(2, "Stokta Yok"));
        this.cmb_f_product_stock.setSelectedItem(null);

        //basket tab

        loadBasketTable();
        loadBasketButtonEvent();
        loadBasketCustomerComba();

        //cart tab
        loadCartTable();
    }
    //-----------------cart tab kodları-----------------------------
    private void loadCartTable() {
        Object[] columnPorduct = {"ID", "Müşteri Adı","Ürün Adı", "Fiyatı", "Sipariş Tarihi", "Not"};

        ArrayList<Cart> carts = this.cartController.findAll();


        //tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_cart.getModel();
        clearModel.setRowCount(0);

        this.tmdl_cart.setColumnIdentifiers(columnPorduct);

        for(Cart cart : carts){
            Object[] rowObject = {
                    cart.getId(),
                    cart.getCustomer().getName(),
                    cart.getProduct().getName(),
                    cart.getPrice(),
                    cart.getDate(),
                    cart.getNote()
            };

            this.tmdl_cart.addRow(rowObject);


        };


        this.tbl_cart.setModel(tmdl_cart);
        this.tbl_cart.getTableHeader().setReorderingAllowed(false);
        this.tbl_cart.getColumnModel().getColumn(0).setMaxWidth(50);
        //this.tbl_product.setEnabled(false);
    }
    //--------------------------------------------------

    //--------------- product kodları-----------------
    private void loadProductButtonEvent() {

        //yeni ürün ekleme
        this.btn_product_new.addActionListener(e -> {
            ProductUI productUI = new ProductUI(new Product());
            productUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadProductTable(null);

                }
            });
        });

        this.btn_product_filter.addActionListener(e -> {
            ArrayList<Product> filteredProducts = this.productController.filter(

                this.fld_f_product_name.getText(),
                this.fld_f_product_code.getText(),
                    (Item)this.cmb_f_product_stock.getSelectedItem()
            );
            loadProductTable(filteredProducts);
        });

        this.btn_product_filter_reset.addActionListener(e -> {
            this.fld_f_product_name.setText(null);
            this.fld_f_product_code.setText(null);
            this.cmb_f_product_stock.setSelectedItem(null);
            loadProductTable(null);

        });



    }

    private void loadProductPopupMenu() {
        this.tbl_product.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int SelectedRow = tbl_product.rowAtPoint(e.getPoint());
                tbl_product.setRowSelectionInterval(SelectedRow, SelectedRow);
            }
        });

        this.popup_product.add("Sepete Ekle").addActionListener(e -> {
            int selectID = Integer.parseInt(this.tbl_product.getValueAt(this.tbl_product.getSelectedRow(), 0).toString());
            Product basketProduct = this.productController.getById(selectID);
            if(basketProduct.getStock() <= 0){
                Helper.showMsg("Bu ürün stokta yoktur !");
            }else{
                Basket basket = new Basket(basketProduct.getId());
                if(this.basketController.save(basket)){
                    Helper.showMsg("done");
                    loadBasketTable();
                }else{
                    Helper.showMsg("error");
                }

            }

        });

        this.popup_product.add("Güncelle").addActionListener(e -> {
            int selectID = Integer.parseInt(this.tbl_product.getValueAt(this.tbl_product.getSelectedRow(), 0).toString());
            ProductUI productUI = new ProductUI(this.productController.getById(selectID));
            productUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadProductTable(null);
                    loadBasketTable();
                }
            });
        });
        this.popup_product.add("Sil").addActionListener(e -> {
            int selectID = Integer.parseInt(this.tbl_product.getValueAt(this.tbl_product.getSelectedRow(), 0).toString());
            if(Helper.confirm("sure")){
                if(this.productController.delete(selectID)){
                    Helper.showMsg("done");
                    loadProductTable(null);
                    loadBasketTable();

                }else{
                    Helper.showMsg("error");
                }
            }
        });

        this.tbl_product.setComponentPopupMenu(this.popup_product);
    }

    private void loadProductTable(ArrayList<Product> products) {
        Object[] columnPorduct = {"ID", "Ürün Adı", "Ürün Kodu", "Fiyatı", "Stok"};
        if(products == null){
            products = this.productController.findAll();
        }

        //tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) tbl_product.getModel();
        clearModel.setRowCount(0);

        this.tmdl_product.setColumnIdentifiers(columnPorduct);

        for(Product product : products){
            Object[] rowObject = {
                    product.getId(),
                    product.getName(),
                    product.getCode(),
                    product.getPrice(),
                    product.getStock()
            };

            this.tmdl_product.addRow(rowObject);

        }

        this.tbl_product.setModel(tmdl_product);
        this.tbl_product.getTableHeader().setReorderingAllowed(false);
        this.tbl_product.getColumnModel().getColumn(0).setMaxWidth(50);
        //this.tbl_product.setEnabled(false);
    }

    //--------------------------------------------


    // --------- customer kodları-------------------
    //buton işlemleri
    private void loadCustomerButtonEvent() {
        //yeni kullanıcı ekleme
        this.btn_customer_new.addActionListener(e -> {
        CustomerUI customerUI = new CustomerUI(new Customer());
        customerUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadCustomerTable(null);
                loadBasketCustomerComba();
                }
            });
        });

        this.btn_customer_filter.addActionListener(e -> {
            ArrayList<Customer> filteredCustomers = this.customerController.filter(
                    this.fld_f_customer_name.getText(),
                    (Customer.TYPE) this.cmb_fcustomer_type.getSelectedItem()
            );
            loadCustomerTable(filteredCustomers);
        });

        this.btn_customer_filter_reset.addActionListener(e -> {
            loadCustomerTable(null);
            this.fld_f_customer_name.setText(null);
            this.cmb_fcustomer_type.setSelectedItem(null);
        });
    }

    private void loadCustomerPopupMenu(){
        this.tbl_customer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int SelectedRow = tbl_customer.rowAtPoint(e.getPoint());
                tbl_customer.setRowSelectionInterval(SelectedRow, SelectedRow);
            }
        });

        this.popup_customer.add("Güncelle").addActionListener(e -> {
            int selectID = Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(), 0).toString());
            Customer editedCustomer = customerController.getById(selectID);
            CustomerUI customerUI = new CustomerUI(editedCustomer);
            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                    loadBasketCustomerComba();
                }
            });
        });



        this.popup_customer.add("Sil").addActionListener(e -> {
            int selectID = Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(), 0).toString());
            if(Helper.confirm("sure")){
                if(this.customerController.delete(selectID)){
                    Helper.showMsg("done");
                    loadCustomerTable(null);
                    loadBasketCustomerComba();

                }else{
                    Helper.showMsg("error");
                }
            }

        });

        this.tbl_customer.setComponentPopupMenu(this.popup_customer);
    }

    private void loadCustomerTable(ArrayList<Customer> customers) {
        Object[] columnCustomer = {"ID", "Müşteri Adı", "Tipi", "Telefon", "E-posta", "Adres"};
        if(customers == null){
            customers = this.customerController.findAll();
        }

        //tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) tbl_customer.getModel();
        clearModel.setRowCount(0);

        this.tmdl_customer.setColumnIdentifiers(columnCustomer);

        for(Customer customer : customers){
            Object[] rowObject = {
                    customer.getId(),
                    customer.getName(),
                    customer.getType(),
                    customer.getPhone(),
                    customer.getMail(),
                    customer.getAddress()};

            this.tmdl_customer.addRow(rowObject);

        }

        this.tbl_customer.setModel(tmdl_customer);
        this.tbl_customer.getTableHeader().setReorderingAllowed(false);
        this.tbl_customer.getColumnModel().getColumn(0).setMaxWidth(50);
        //this.tbl_customer.setEnabled(false);
    }

    //--------------------------------------------


    //--------------- basket kodları-----------------
    private void loadBasketTable() {
        Object[] columnPorduct = {"ID", "Ürün Adı", "Ürün Kodu", "Fiyatı", "Stok"};

        ArrayList<Basket> baskets = this.basketController.findAll();


        //tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) tbl_basket.getModel();
        clearModel.setRowCount(0);

        this.tmdl_basket.setColumnIdentifiers(columnPorduct);
        int totalPrice = 0;
        for(Basket basket : baskets){
            Object[] rowObject = {
                    basket.getId(),
                    basket.getProduct().getName(),
                    basket.getProduct().getCode(),
                    basket.getProduct().getPrice(),
                    basket.getProduct().getStock()
            };

            this.tmdl_basket.addRow(rowObject);

            totalPrice += basket.getProduct().getPrice();
        }
        this.lbl_basket_price.setText(totalPrice+ " " + "TL");
        this.lbl_basket_count.setText(baskets.size() + " " + "Adet");

        this.tbl_basket.setModel(tmdl_basket);
        this.tbl_basket.getTableHeader().setReorderingAllowed(false);
        this.tbl_basket.getColumnModel().getColumn(0).setMaxWidth(50);
        //this.tbl_product.setEnabled(false);
    }

    private void loadBasketButtonEvent() {
        this.btn_basket_reset.addActionListener(e -> {
            if(this.basketController.clear()){
                Helper.showMsg("done");
                loadBasketTable();

            }else{
                Helper.showMsg("error");
            }

        });
        this.btn_basket_new.addActionListener(e -> {
            Item selectedCustomer = (Item) cmb_basket_customer.getSelectedItem();
            if(selectedCustomer == null){
                Helper.showMsg("Lütfen Bir Müşteri Seçiniz !");
            }else{
                Customer customer = this.customerController.getById(selectedCustomer.getKey());
                ArrayList<Basket> baskets = this.basketController.findAll();

                if(customer.getId() == 0){
                    Helper.showMsg("Böyle Müşteri Bulunamadı");
                }else if(baskets.size() == 0){
                    Helper.showMsg("Lütfen Sepete Bir Ürün Ekleyin");
                }else{
                    CartUI cartUI = new CartUI(customer);
                    cartUI.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            loadBasketTable();
                            loadProductTable(null);
                            loadCartTable();
                        }
                    });
                }
            }
        });
    }

    private void loadBasketCustomerComba(){
        ArrayList<Customer> customers = this.customerController.findAll();
        this.cmb_basket_customer.removeAllItems();
        for(Customer customer : customers){
            int comboKey = customer.getId();
            String comboValue = customer.getName();
            this.cmb_basket_customer.addItem(new Item(comboKey, comboValue));
        }

        this.cmb_basket_customer.setSelectedItem(null);
    }
    //--------------------------------------------



}
