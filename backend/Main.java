import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import com.sun.net.httpserver.HttpServer;

public class Main{
    public static void main(String[] args) throws Exception{
        String url = "jdbc:mysql://localhost:3306/Encave";
        String user = "encave";
        String password = "password";

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/AddUser", new AddUserHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Server started at http://localhost:8000");
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();          
        }
        catch (Exception e) {
            //code for error
            System.out.println("Error");
        }
    }
}