import javax.swing.*;
import java.util.HashMap;
import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        System.out.println("Hello World");
        JFrame frame = new JFrame("Encave System");
        frame.setSize(800,600);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        Scanner scan = new Scanner(System.in);
        HashMap<String, String> map = new HashMap<String, String>();
        while(true){
            System.out.print("Enter username: ");
            String username = scan.nextLine();
            System.out.print("Enter password: ");
            String password = scan.nextLine();
            if(map.get(username) == null){
                map.put(username, password);
                System.out.println("Successfully added new user!");
            }
            else{
                if(password != map.get(username)){
                    System.out.println("Wrong Password");
                }
                else{
                    System.out.println("Welcome back " + username + "!");
                }
            }
        }
    }
}