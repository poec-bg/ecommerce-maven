package services.db;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DBService {

    private static DBService instance;
    private Connection connection = null;

    private DBService() {
        String adresseIpMysqlServer;
        if(isMaison()){
            adresseIpMysqlServer = "192.168.1.6";
        }
        else{
            adresseIpMysqlServer = "10.110.10.27";
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + adresseIpMysqlServer + "/lenaick?user=root&password=formation&useSSL=false");
        } catch (SQLException sqle) {
            System.out.println("Impossible d'établir de connection avec la base de données");
            System.out.println("jdbc:mysql://" + adresseIpMysqlServer + "/lenaick?user=root&password=formation&useSSL=false");
            System.out.println(sqle.getMessage());
            System.exit(0);
        }
        catch (ClassNotFoundException cnfe){
            System.out.println(cnfe.getMessage());
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

    private boolean isMaison(){

        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()){
                Enumeration<InetAddress> i = e.nextElement().getInetAddresses();
                while (i.hasMoreElements()){
                    InetAddress a = i.nextElement();
                    if(a.isSiteLocalAddress()){
                        if(a.getHostAddress().toString().equals("192.168.1.1")){
                            return true;
                        }
                    }
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return false;
    }
}
