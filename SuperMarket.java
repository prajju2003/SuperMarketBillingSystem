import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SuperMarket extends JFrame {
    private JTextField productCodeField, quantityField;
    private JTextArea receiptArea;
    private JButton addToCartButton, generateReceiptButton;

    private Connection connection;

    public SuperMarket() {
        initializeGUI();
        initializeDatabase();
    }

    private void initializeGUI() {
        setTitle("Supermarket Billing System");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        productCodeField = new JTextField(10);
        quantityField = new JTextField(5);
        receiptArea = new JTextArea(15, 30);
        receiptArea.setEditable(false);
        addToCartButton = new JButton("Add to Cart");
        generateReceiptButton = new JButton("Generate Receipt");

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToCart();
            }
        });

        generateReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReceipt();
            }
        });

        panel.add(new JLabel("Product Code:"));
        panel.add(productCodeField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(addToCartButton);
        panel.add(generateReceiptButton);
        panel.add(new JScrollPane(receiptArea));

        add(panel);
        setVisible(true);
    }

    private void initializeDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/supermarket";
            String userName = "root";
            String password = "root";

            this.connection = DriverManager.getConnection(url, userName, password); // Assign to instance variable

            // Create a table for products if it doesn't exist
            this.connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS products (" +
                            "code INT PRIMARY KEY, " +
                            "name VARCHAR(255), " +
                            "price DOUBLE)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToCart() {
        int productCode = Integer.parseInt(productCodeField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM products WHERE code = ?");
            preparedStatement.setInt(1, productCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String productName = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                double total = price * quantity;

                receiptArea.append(productName + " x " + quantity + " - $ " + total + "\n");
            } else {
                receiptArea.append("Product not found!\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void generateReceipt() {
        double totalAmount = 0.0;
        receiptArea.append("Receipt:\n");

        // Iterate through each line in the receiptArea and calculate the total amount
        String[] lines = receiptArea.getText().split("\n");
        for (String line : lines) {
            String[] parts = line.split(" - ");
            if (parts.length == 2) {
                double price = Double.parseDouble(parts[1].substring(1)); // Remove "$" sign
                totalAmount += price;
                receiptArea.append(line + "\n");
            }
        }

        receiptArea.append("Total: $" + totalAmount + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SuperMarket();
            }
        });
    }
}