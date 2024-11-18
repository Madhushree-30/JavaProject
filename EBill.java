import java.sql.*;
import java.util.Scanner;

// Customer class represents a customer with an ID and name
class Customer {
    protected int customerId;  // Customer's unique ID
    protected String customerName;  // Customer's name

    // Constructor to initialize customer details
    public Customer(int customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }

    // Getter method for customerId
    public int getCustomerId() {
        return customerId;
    }

    // Getter method for customerName
    public String getCustomerName() {
        return customerName;
    }
}

// ElectricityBill class extends Customer and adds billing functionality
class ElectricityBill extends Customer {
    private int unitsConsumed;  // Number of units consumed by the customer

    // Constructor to initialize electricity bill details
    public ElectricityBill(int customerId, String customerName, int unitsConsumed) {
        super(customerId, customerName);  // Call the parent class constructor
        this.unitsConsumed = unitsConsumed;
    }

    // Method to calculate the electricity bill amount based on units consumed
    public double calculateAmount() {
        double amount = 0;

        // Calculate bill based on consumption slabs
        if (unitsConsumed <= 50) {
            amount = 0.50 * unitsConsumed;  // Rs. 0.50 per unit for first 50 units
        } else if (unitsConsumed <= 150) {
            amount = (50 * 0.5) + (unitsConsumed - 50) * 0.75;  // Rs. 0.75 per unit for 51-150 units
        } else if (unitsConsumed < 250) {
            amount = (unitsConsumed - 150) * 1.20 + (50 * 0.50) + (100 * 0.75);  // Rs. 1.20 per unit for 151-250 units
        } else {
            amount = (unitsConsumed - 250) * 1.50 + (100 * 1.20) + (100 * 0.75) + (50 * 0.5);  // Rs. 1.50 per unit for more than 250 units
        }

        return amount;  // Return the calculated amount
    }

    // Method to calculate a surcharge (20% of the amount)
    public double calculateSurcharge(double amount) {
        return 0.2 * amount;
    }

    // Method to calculate total amount including surcharge
    public double calculateTotalAmount(double amount, double surcharge) {
        return amount + surcharge;  // Return the total (amount + surcharge)
    }

    // Method to print the electricity bill and save it to the database
    public void printBill() {
        double amount = calculateAmount();  // Calculate the amount
        double surcharge = calculateSurcharge(amount);  // Calculate surcharge
        double totalAmount = calculateTotalAmount(amount, surcharge);  // Calculate total amount

        // Display the bill details to the user
        System.out.println("\n--- Electricity Bill ---");
        System.out.println("Customer ID: " + getCustomerId());
        System.out.println("Customer Name: " + getCustomerName());
        System.out.println("Units Consumed: " + unitsConsumed);
        System.out.println("Amount: Rs. " + String.format("%.2f", amount));
        System.out.println("Surcharge (20%): Rs. " + String.format("%.2f", surcharge));
        System.out.println("Total Amount: Rs. " + String.format("%.2f", totalAmount));

        saveBillToDatabase(amount, surcharge, totalAmount);  // Save the bill details to the database
    }

    // Method to save the bill details to the database
    public void saveBillToDatabase(double amount, double surcharge, double totalAmount) {
        String url = "jdbc:mysql://localhost:3306/ElectricityBilling";  // Database URL
        String user = "root";  // Database username
        String password = "30-Jun-06";  // Database password (change as needed)

        // SQL query to insert bill details into the Bills table
        String sql = "INSERT INTO Bills (customerId, customerName, unitsConsumed, amount, surcharge, totalAmount) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection and execute the query
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                // Set parameters for the SQL query
                stmt.setInt(1, getCustomerId());
                stmt.setString(2, getCustomerName());
                stmt.setInt(3, unitsConsumed);
                stmt.setDouble(4, amount);
                stmt.setDouble(5, surcharge);
                stmt.setDouble(6, totalAmount);

                // Execute the update and check if successful
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Bill data saved to the database successfully.");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error saving bill data to the database: " + e.getMessage());
        }
    }
}

public class EBill {
    public static void main(String[] args) {
        // Create a scanner to get user input
        Scanner scanner = new Scanner(System.in);

        // Ask for customer details
        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine();  // Consume newline character

        System.out.print("Enter Customer Name: ");
        String customerName = scanner.nextLine();

        System.out.print("Enter Units Consumed: ");
        int unitsConsumed = scanner.nextInt();

        // Create an ElectricityBill object and print the bill
        ElectricityBill bill = new ElectricityBill(customerId, customerName, unitsConsumed);
        bill.printBill();  // Print the bill details
    }
}
