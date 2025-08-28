import businness.UserController;
import core.Database;
import core.Helper;
import entity.User;
import view.DashboardUI;
import view.LoginUI;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.*;
public class App {
    public static void main(String[] args) {
        Helper.setTheme();
        LoginUI loginUI = new LoginUI();
        //UserController userController = new UserController();
        //DashboardUI dashboardUI = new DashboardUI(user);

    }
}
