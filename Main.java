import javax.swing.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Encave");
        frame.setSize(400, 500);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 20, 100, 30);
        frame.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(130, 20, 200, 30);
        frame.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 60, 100, 30);
        frame.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(130, 60, 200, 30);
        frame.add(passField);

        JLabel empLabel = new JLabel("Add Employee (ID, FName, LName, Salary):");
        empLabel.setBounds(30, 150, 300, 30);
        empLabel.setVisible(false);
        frame.add(empLabel);

        JTextField dataField = new JTextField();
        dataField.setBounds(30, 190, 300, 30);
        dataField.setVisible(false);
        frame.add(dataField);

        JButton submitBtn = new JButton("Submit to DB");
        submitBtn.setBounds(30, 230, 150, 30);
        submitBtn.setVisible(false);
        frame.add(submitBtn);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(130, 100, 100, 30);
        frame.add(loginBtn);

        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if(user.equals("chaitanya") && pass.equals("password")) {
                JOptionPane.showMessageDialog(frame, "Login Successful");
                empLabel.setVisible(true);
                dataField.setVisible(true);
                submitBtn.setVisible(true);
            } 
            else{
                JOptionPane.showMessageDialog(frame, "Invalid Credentials");
            }
        });

        submitBtn.addActionListener(e -> {
            String[] details = dataField.getText().split(" ");
            if(details.length == 4) {
                runPythonAction("employee", details[0], details[1], details[2], details[3]);
            } 
            else{
                JOptionPane.showMessageDialog(frame, "Enter: ID FName LName Salary");
            }
        });

        frame.setVisible(true);
    }

    public static void runPythonAction(String task, String... args) {
        try{
            String[] command = new String[args.length + 3];
            command[0] = "python";
            command[1] = "main.py";
            command[2] = task;
            System.arraycopy(args, 0, command, 3, args.length);

            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String result = in.readLine();
            System.out.println("Python says: " + result);
            
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}