package services.db;

import com.google.common.base.Strings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBService {

    private static DBService instance;
    private Connection connection = null;

    private DBService() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String ip = System.getenv().get("DB_PORT_3306_TCP_ADDR");
            if (Strings.isNullOrEmpty(ip)) {
                ip = "10.110.10.13";
//                ip = "10.0.0.21";
            }
            connection = DriverManager.getConnection("jdbc:mysql://" + ip + "/nicogiard?user=root&password=formation&useSSL=false");
        } catch (ClassNotFoundException | SQLException e) {
            System.exit(0);
        }
    }

    public static DBService get() {
        if (instance == null) {
            instance = new DBService();
        }
        return instance;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
