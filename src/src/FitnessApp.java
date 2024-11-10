package src;
import java.util.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


 class HashingException extends Exception {
    public HashingException(String message, Throwable cause) {
        super(message, cause);
    }
} 
//Utility class for security-related functions
class SecurityUtils {
    // Private constructor to prevent instantiation
    private SecurityUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }   
    public static String hashPassword(String password) throws HashingException  {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] byteData = md.digest();
    
            StringBuilder sb = new StringBuilder();
            for (byte b : byteData) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new HashingException("Error hashing password", e);
        }
    }
}


// Define the FitnessPlan class to represent a fitness plan
class FitnessPlan {
    String category; // Category of the fitness plan (e.g., Cardio, Strength Training)
    int minDuration; // Minimum recommended duration per week in minutes
    String minFitnessLevel; // Minimum required fitness level (Beginner, Intermediate, Advanced)
    String healthGoal; // Health goal associated with the fitness plan

    // Constructor to initialize a fitness plan
    public FitnessPlan(String category, int minDuration, String minFitnessLevel, String healthGoal) {
        this.category = category;
        this.minDuration = minDuration;
        this.minFitnessLevel = minFitnessLevel;
        this.healthGoal = healthGoal;
    }

    // Method to display the details of the fitness plan
    public void displayPlan() {
        System.out.println(category + ":");
        System.out.println("  - Minimum recommended duration per week: " + minDuration + " minutes");
        System.out.println("  - Minimum required fitness level: " + minFitnessLevel);
        System.out.println("  - Health goal: " + healthGoal);
        System.out.println();
    }
}

// Define the User class to capture user input and details
class User {
    List<String> fitnessGoals; // List to store multiple fitness goals
    String currentFitnessLevel; // User's current fitness level
    String age; // User's age
    String illnesses; // Any illnesses the user may have
    String surgeries; // Any surgeries the user may have
    String username; // User's name
    String password; // User's password
    String email; // User's email

    // Constructor to initialize user details
    public User(String username, String password, String email, List<String> fitnessGoals, String currentFitnessLevel, String age, String illnesses, String surgeries) throws HashingException    {
        this.username = username;
        this.password = SecurityUtils.hashPassword(password); // Hash the password
 // Store the password (in a real app, hash it)
        this.email = email; // Store email
        this.fitnessGoals = fitnessGoals; // Store fitness goals
        this.currentFitnessLevel = currentFitnessLevel; // Store current fitness level
        this.age = age; // Store age
        this.illnesses = illnesses; // Store illnesses
        this.surgeries = surgeries; // Store surgeries
    }

    // Method to save user information to a file
    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write("Username: " + username);
            writer.write(", Password: " + password);
            writer.write(", Email: " + email);
            writer.write(", Age: " + age);
            writer.write(", Fitness Level: " + currentFitnessLevel);
            writer.write(", Goals: " + String.join(", ", fitnessGoals));
            writer.write(", Illnesses: " + illnesses);
            writer.write(", Surgeries: " + surgeries);
            writer.newLine(); // Move to the next line after writing user details
        } catch (IOException e) {
            System.out.println("Error saving user information: " + e.getMessage());
        }
    }

    // Method to validate email using regex
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }
}

public class FitnessApp {
    
    // Initialize available fitness plans
    private static List<FitnessPlan> initializeFitnessPlans() {
        List<FitnessPlan> plans = new ArrayList<>();
        // Add predefined fitness plans to the list
        plans.add(new FitnessPlan("Cardio", 150, "Beginner", "Weight Loss"));
        plans.add(new FitnessPlan("Strength Training", 120, "Intermediate", "Muscle Building"));
        plans.add(new FitnessPlan("Flexibility", 90, "Beginner", "Improve Flexibility"));
        plans.add(new FitnessPlan("HIIT", 90, "Advanced", "Improve Cardiovascular Health"));
        plans.add(new FitnessPlan("Yoga", 120, "Beginner", "Stress Relief"));
        return plans; // Return the list of fitness plans
    }

    // Match fitness plans based on user input
    private static List<FitnessPlan> matchFitnessPlans(User user, List<FitnessPlan> plans) {
        List<FitnessPlan> matchedPlans = new ArrayList<>();
        Set<String> selectedCategories = new HashSet<>(); // Track selected categories to avoid duplicates
        
        for (FitnessPlan plan : plans) {
            // Check if the plan matches the user's goals and fitness level
            if (user.fitnessGoals.contains(plan.healthGoal) &&
                (user.currentFitnessLevel.equalsIgnoreCase(plan.minFitnessLevel) ||
                 user.currentFitnessLevel.equalsIgnoreCase("Advanced")) &&
                !selectedCategories.contains(plan.category)) {
                
                matchedPlans.add(plan); // Add matched plan to the list
                selectedCategories.add(plan.category); // Mark the category as selected
            }
        }
        return matchedPlans; // Return the list of matched fitness plans
    }

    // Calculate the total required exercise time based on user level
    private static int calculateWeeklyExerciseTime(User user, List<FitnessPlan> matchedPlans) {
        int baseTime = 120; // Minimum required time
        int extraTime = 0; // Extra time based on fitness level

        // Determine extra time based on the user's fitness level
        switch (user.currentFitnessLevel.toLowerCase()) {
            case "beginner":
                extraTime = 30; // Add 30 minutes for beginners
                break;
            case "intermediate":
                extraTime = 20; // Add 20 minutes for intermediates
                break;
            case "advanced":
                extraTime = 10; // Add 10 minutes for advanced users
                break;
            default:
                extraTime = 0; // No extra time for invalid levels
                break;
        }

        // Calculate total weekly exercise time based on matched plans
        int totalTime = baseTime + (extraTime * matchedPlans.size());
        return totalTime; // Return total exercise time
    }

    // Display matched fitness plans
    private static void displayMatchedFitnessPlans(List<FitnessPlan> matchedPlans) {
        if (matchedPlans.isEmpty()) {
            System.out.println("\nNo matching fitness plans found based on your input.");
        } else {
            System.out.println("\n--- Matched Fitness Plans ---");
            for (FitnessPlan plan : matchedPlans) {
                plan.displayPlan(); // Display each matched fitness plan
            }
        }
    }

    // Validate user fitness level input
    private static boolean isValidFitnessLevel(String fitnessLevel) {
        return fitnessLevel.equalsIgnoreCase("Beginner") ||
               fitnessLevel.equalsIgnoreCase("Intermediate") ||
               fitnessLevel.equalsIgnoreCase("Advanced");
    }

    // Validate age input
    private static boolean isValidAge(String age) {
        return age.matches("\\d+") && Integer.parseInt(age) > 0 && Integer.parseInt(age) < 130; // Check if age is valid
    }

    // Check if username already exists
    private static boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the username is already in use
                if (line.contains("Username: " + username)) {
                    return true; // Username is taken
                }
            }
        } catch (IOException e) {
            System.out.println("Error checking username: " + e.getMessage());
        }
        return false; // Username is available
    }

    // Check if email is already taken
    private static boolean isEmailTaken(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the email is already in use
                if (line.contains(", Email: " + email)) {
                    return true; // Email is already taken
                }
            }
        } catch (IOException e) {
            System.out.println("Error checking email: " + e.getMessage());
        }
        return false; // Email is available
    }

    // Check if password is already taken
    private static boolean isPasswordTaken(String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the password is already in use
                if (line.contains(", Password: " + password)) {
                    return true; // Password is already taken
                }
            }
        } catch (IOException e) {
            System.out.println("Error checking password: " + e.getMessage());
        }
        return false; // Password is available
    }

    // Main method to run the application
    public static void main(String[] args) throws Exception   {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Display all available fitness plans
        List<FitnessPlan> availablePlans = initializeFitnessPlans();
        System.out.println("\n--- Available Fitness Plans ---");
        for (FitnessPlan plan : availablePlans) {
            plan.displayPlan(); // Display each fitness plan
        }

        // Step 2: Register user information
        String username;
        do {
            System.out.println("Enter a username for registration:");
            username = scanner.nextLine();
            if (isUsernameTaken(username)) {
                System.out.println("Username is already taken. Please choose another.");
            }
        } while (isUsernameTaken(username)); // Ensure unique username

        String password;
        do {
            System.out.println("Enter a password:");
            password = scanner.nextLine(); // Get user password
            if (isPasswordTaken(password)) {
                System.out.println("Password is already taken. Please choose another.");
            } else if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please try again.");
            }
        } while (isPasswordTaken(password) || password.isEmpty()); // Ensure password is not empty and unique

        String confirmPassword;
        do {
            System.out.println("Confirm your password:");
            confirmPassword = scanner.nextLine();
            if (!password.equals(confirmPassword)) {
                System.out.println("Passwords do not match. Please try again:");
            }
        } while (!password.equals(confirmPassword)); // Ensure passwords match

        String email;
        do {
            System.out.println("Enter your email:");
            email = scanner.nextLine();
            if (!new User("", "", email, null, "", "", "", "").isValidEmail(email)) {
                System.out.println("Invalid email format. Please enter a valid email.");
            } else if (isEmailTaken(email)) {
                System.out.println("Email is already taken. Please choose another.");
            }
        } while (!new User("", "", email, null, "", "", "", "").isValidEmail(email) || isEmailTaken(email));

        // Step 3: Get fitness goals from user
        List<String> fitnessGoals = new ArrayList<>();
        boolean validGoals = false;
        List<String> availableGoals = Arrays.asList("Weight Loss", "Muscle Building", "Improve Flexibility", "Stress Relief", "Improve Cardiovascular Health");

        while (!validGoals) {
            System.out.println("Choose your fitness goals from the following options:");
            for (String goal : availableGoals) {
                System.out.println(" - " + goal);
            }
            System.out.println("Enter your goals separated by commas:");

            String[] goalsInput = scanner.nextLine().split(",");
            fitnessGoals.clear();

            for (String goal : goalsInput) {
                String trimmedGoal = goal.trim();
                if (availableGoals.contains(trimmedGoal)) {
                    fitnessGoals.add(trimmedGoal); // Add valid goals to the list
                }
            }

            if (fitnessGoals.isEmpty()) {
                System.out.println("Invalid input. Please enter at least one valid fitness goal from the list.");
            } else {
                validGoals = true; // Valid goals have been entered
            }
        }

        // Get user fitness level
        String fitnessLevel;
        do {
            System.out.println("Enter your current fitness level (Beginner, Intermediate, Advanced):");
            fitnessLevel = scanner.nextLine();
            if (!isValidFitnessLevel(fitnessLevel)) {
                System.out.println("Invalid input. Please enter Beginner, Intermediate, or Advanced.");
            }
        } while (!isValidFitnessLevel(fitnessLevel));

        // Get user age
        String age;
        do {
            System.out.println("Enter your age:");
            age = scanner.nextLine();
            if (!isValidAge(age)) {
                System.out.println("Invalid age. Please enter a valid number between 1 and 129.");
            }
        } while (!isValidAge(age));

        // Get user medical history
        String illnesses;
        do {
            System.out.println("Enter any illnesses (if none, type 'None'):");
            illnesses = scanner.nextLine();
            if (illnesses.trim().isEmpty()) {
                System.out.println("Invalid input. This field cannot be empty.");
            }
        } while (illnesses.trim().isEmpty());
        
        String surgeries;
        do {
            System.out.println("Enter any surgeries (if none, type 'None'):");
            surgeries = scanner.nextLine();
            if (surgeries.trim().isEmpty()) {
                System.out.println("Invalid input. This field cannot be empty.");
            }
        } while (surgeries.trim().isEmpty());

        // Step 4: Create user object
        User user = new User(username, password, email, fitnessGoals, fitnessLevel, age, illnesses, surgeries);
        
        // Save user information to file
        user.saveToFile();

        // Step 5: Match fitness plans based on user input
        List<FitnessPlan> matchedPlans = matchFitnessPlans(user, availablePlans);

        // Step 6: Calculate total exercise time
        int totalExerciseTime = calculateWeeklyExerciseTime(user, matchedPlans);

        // Step 7: Display output
        displayMatchedFitnessPlans(matchedPlans);
        System.out.println("Total Weekly Exercise Time: " + totalExerciseTime + " minutes");
        System.out.println("Additional Notes: Consider consulting a healthcare professional based on your medical history.");

        // Close the scanner to prevent resource leaks
        scanner.close();
    }
}
