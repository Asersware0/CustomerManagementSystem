package core;

import java.sql.Connection;
import java.sql.Connection.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    //singleton design pattern

    private static Database instance = null;
    private Connection connection = null;
    private final String DB_URL = "jdbc:mariadb://localhost:3306/siparistakip?user=********&password=*******";
    private final String DB_USER = "kullanıcı adınız";
    private final String DB_PASSWORD = "şifreniz";

    private Database(){
        try {
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private Connection getConnection() {
        return connection;
    }

    public static Connection getInstance(){
        try {
            if((instance == null) || instance.getConnection().isClosed()){
                instance = new Database();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return instance.getConnection();
    }



}
