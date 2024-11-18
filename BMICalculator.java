import java.sql.*;
import java.util.Scanner;

public class BMICalculator {

    // Method to calculate BMI
    public static double calculateBMI(double weight, double height) {
        if (height <= 0 || weight <= 0) {
            return 0.0;
        }
        return weight / (height * height);  // BMI formula
    }

    // Method to determine the BMI category
    public static String getBMICategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi >= 18.5 && bmi < 24.9) {
            return "Normal weight";
        } else if (bmi >= 25 && bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    // Method to insert BMI data into the database
    public static void insertBMIData(String name, double weight, double height, double bmi, String category) {
        String url = "jdbc:mysql://localhost:3306/bmi4_calculator";  // Use the new database name here
        String username = "root";  // Change to your MySQL username
        String password = "16-Dec-04";  // Change to your MySQL password

        // SQL query to insert data into the table
        String query = "INSERT INTO bmi3_records (name, weight, height, bmi, category) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the parameters for the prepared statement
            statement.setString(1, name);
            statement.setDouble(2, weight);
            statement.setDouble(3, height);
            statement.setDouble(4, bmi);
            statement.setString(5, category);

            // Execute the insert operation
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("BMI data inserted successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to display BMI records based on the user's name
    public static void displayBMIRecordsByName(String name) {
        String url = "jdbc:mysql://localhost:3306/bmi4_calculator";  // Use the new database name here
        String username = "root";  // Change to your MySQL username
        String password = "16-Dec-04";  // Change to your MySQL password

        // SQL query to select BMI records by name
        String query = "SELECT * FROM bmi3_records WHERE name = ?";  // Filter records by name

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the parameter for the prepared statement
            statement.setString(1, name);

            // Execute the query
            try (ResultSet resultSet = statement.executeQuery()) {

                // Display the records
                System.out.println("ID | Name | Weight | Height | BMI | Category");
                boolean hasRecords = false;
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String userName = resultSet.getString("name");
                    double weight = resultSet.getDouble("weight");
                    double height = resultSet.getDouble("height");
                    double bmi = resultSet.getDouble("bmi");
                    String category = resultSet.getString("category");

                    System.out.println(id + " | " + userName + " | " + weight + " | " + height + " | " + bmi + " | " + category);
                    hasRecords = true;
                }

                if (!hasRecords) {
                    System.out.println("No records found for the specified user.");
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Display options to the user
        System.out.println("Select an option:");
        System.out.println("1. Create a new user and record BMI.");
        System.out.println("2. Display BMI records for a specific user.");
        int option = scanner.nextInt();

        switch (option) {
            case 1:
                // Option 1: Create a new user and record BMI
                scanner.nextLine();  // Consume the newline character from nextInt()

                System.out.println("Enter your name: ");
                String name = scanner.nextLine();

                System.out.println("Enter your weight (in kg): ");
                double weight = scanner.nextDouble();

                System.out.println("Enter your height (in meters): ");
                double height = scanner.nextDouble();

                // Validate height
                if (height < 0.5 || height > 3) {
                    System.out.println("Invalid height. Please enter a height between 0.5 meters and 3 meters.");
                    break; // Exit the switch case
                }

                // Calculate BMI
                double bmi = calculateBMI(weight, height);

                // Handle invalid BMI calculation
                if (bmi == 0.0) {
                    System.out.println("BMI calculation failed. Please ensure that weight and height are valid.");
                    break; // Exit the switch case
                }

                // Get BMI category
                String category = getBMICategory(bmi);

                // Print BMI and category
                System.out.printf("Your BMI is: %.2f\n", bmi);
                System.out.println("BMI Category: " + category);

                // Insert the data into the database
                insertBMIData(name, weight, height, bmi, category);
                break;

            case 2:
                // Option 2: Display BMI records for a specific user
                scanner.nextLine();  // Consume the newline character from nextInt()

                System.out.println("Enter the name to search for BMI records: ");
                String searchName = scanner.nextLine();

                System.out.println("\nBMI Records for " + searchName + ":");
                displayBMIRecordsByName(searchName);
                break;

            default:
                System.out.println("Invalid option. Please select either option 1 or 2.");
                break;
        }

        scanner.close();
    }
}
