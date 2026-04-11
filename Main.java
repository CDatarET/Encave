import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Encave");
        frame.setSize(1000, 800);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(350, 100, 100, 30);
        frame.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(450, 100, 200, 30);
        frame.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(350, 150, 100, 30);
        frame.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(450, 150, 200, 30);
        frame.add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(450, 200, 95, 30);
        frame.add(loginBtn);

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(350 + 205, 200, 95, 30);
        frame.add(signupBtn);

        JLabel empLabel = new JLabel("Add Employee (ID, FName, LName, Salary):");
        empLabel.setBounds(350, 450, 300, 30);
        empLabel.setVisible(false);
        frame.add(empLabel);

        JTextField dataField = new JTextField();
        dataField.setBounds(350, 490, 300, 30);
        dataField.setVisible(false);
        frame.add(dataField);

        JButton submitBtn = new JButton("Submit to DB");
        submitBtn.setBounds(350, 530, 150, 30);
        submitBtn.setVisible(false);
        frame.add(submitBtn);

        /*
        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            
            String response = addUser("login", user, pass);
            if ("SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");
                empLabel.setVisible(true);
                dataField.setVisible(true);
                submitBtn.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.");
            }
        });
        */

        signupBtn.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
            JTextField prnF = new JTextField();
            JTextField fnF = new JTextField();
            JTextField lnF = new JTextField();
            JTextField emF = new JTextField();
            JTextField phF = new JTextField();
            JPasswordField psF = new JPasswordField();

            panel.add(new JLabel("PRN:")); panel.add(prnF);
            panel.add(new JLabel("First Name:")); panel.add(fnF);
            panel.add(new JLabel("Last Name:")); panel.add(lnF);
            panel.add(new JLabel("Email:")); panel.add(emF);
            panel.add(new JLabel("Phone:")); panel.add(phF);
            panel.add(new JLabel("Password:")); panel.add(psF);

            int result = JOptionPane.showConfirmDialog(frame, panel, "Create New Account", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String prn = prnF.getText();
                String fname = fnF.getText();
                String lname = lnF.getText();
                String email = emF.getText();
                String phone = phF.getText();
                String password = new String(psF.getPassword());

                if(prn.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "PRN and Password are required!");
                    return;
                }

                String response = addUser(prn, fname, lname, email, phone, password);
                JOptionPane.showMessageDialog(frame, response);
            }
});
        /*
        submitBtn.addActionListener(e -> {
            String[] d = dataField.getText().split(" ");
            if (d.length == 4) {
                addUser("employee", d[0], d[1], d[2], d[3]);
                JOptionPane.showMessageDialog(frame, "Employee Added!");
            } else {
                JOptionPane.showMessageDialog(frame, "Format: ID FName LName Salary");
            }
        });
        */

        frame.setVisible(true);
    }

    public static String addUser(String prn, String fname, String lname, String email, String phone, String password) {
        try {
            String[] command = new String[] {"python", "addUser.py", prn, fname, lname, email, phone, password};

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            Process p = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                output.append(line).append("\n");
            }

            p.waitFor();

            return output.toString().trim();

        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
}