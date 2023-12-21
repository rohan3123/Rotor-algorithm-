/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orchestator;

/**
 *
 * @author Rohan
 */
import com.sun.faces.util.Json;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.json.JsonException;
import javax.json.JsonObject;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.json.JsonArray;
import javax.json.JsonReaderFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.annotation.PreDestroy;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ws.rs.core.MultivaluedMap;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

@Path("/orchestrator")
public class Orchestrator {

    private static final String DB_PATH = "jdbc:sqlite:EmployeeDatabase.db";
    private static Connection connection;

    static {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Open the connection when the class is loaded
            connection = DriverManager.getConnection(DB_PATH);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    @GET
    @Path("/createDatabase")
    @Produces(MediaType.APPLICATION_JSON)
    public String createDatabase() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to SQLite database
            try (Connection connection = DriverManager.getConnection(DB_PATH)) {
                System.out.println("Connected to database: " + DB_PATH);

            String createUserTableQuery = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT NOT NULL,"
                    + "password TEXT NOT NULL,"
                    + "hours INTEGER NOT NULL DEFAULT 0);";


                try (PreparedStatement createUserTableStatement = connection.prepareStatement(createUserTableQuery);) {
                    createUserTableStatement.execute();
                }
            }

            return "{ \"status\": \"Database created successfully\" }";
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "{ \"status\": \"Failed to create Database. Error: " + e.getMessage() + "\" }";
        }
    }


    @GET
    @Path("/checkDatabaseConnection")
    @Produces(MediaType.APPLICATION_JSON)
    public String checkDatabaseConnection() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to SQLite database
            try (Connection connection = DriverManager.getConnection(DB_PATH)) {
                System.out.println("Connected to database: " + DB_PATH);
            }

            return "{ \"status\": \"Connected to Database\" }";
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "{ \"status\": \"Failed to connect to Database. Error: " + e.getMessage() + "\" }";
        }
    }
    
    @POST
    @Path("/createAdmin")
    @Produces(MediaType.APPLICATION_JSON)
    public String createAdmin() {
        try {
            // Connect to SQLite database
            try (Connection connection = DriverManager.getConnection(DB_PATH)) {
                // Check if the admin user already exists
                String checkAdminQuery = "SELECT id FROM users WHERE username = 'admin'";
                try (PreparedStatement checkAdminStatement = connection.prepareStatement(checkAdminQuery)) {
                    ResultSet resultSet = checkAdminStatement.executeQuery();

                    if (resultSet.next()) {
                        // Admin user already exists
                        return "{ \"status\": \"Admin user already exists\" }";
                    }
                }

                // Insert admin user into the users table
                String insertAdminQuery = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";
                try (PreparedStatement insertAdminStatement = connection.prepareStatement(insertAdminQuery)) {
                    insertAdminStatement.setInt(1, 12345);
                    insertAdminStatement.setString(2, "admin");
                    insertAdminStatement.setString(3, "admin");
                    insertAdminStatement.executeUpdate();
                }

                return "{ \"status\": \"Admin user created successfully\" }";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{ \"status\": \"Failed to create admin user. Error: " + e.getMessage() + "\" }";
        }
    }


    @GET
    @Path("/getAllUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        try {
            // Connect to SQLite database
            try (Connection connection = DriverManager.getConnection(DB_PATH)) {
                // Select all users from the users table
                String getAllUsersQuery = "SELECT * FROM users";
                try (PreparedStatement getAllUsersStatement = connection.prepareStatement(getAllUsersQuery)) {
                    ResultSet resultSet = getAllUsersStatement.executeQuery();

                    while (resultSet.next()) {
                        int userId = resultSet.getInt("id");
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");

                        User user = new User(userId, username, password);
                        userList.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }
    
    @POST
    @Path("/registerUser")
    @Produces(MediaType.APPLICATION_JSON)
    public String registerUser(@FormParam("username") String username, @FormParam("password") String password) {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Check if the database exists, and create it if not
            createDatabase();

            // Connect to SQLite database
            try (Connection connection = DriverManager.getConnection(DB_PATH)) {
                // Generate a unique user ID
                int userId = generateUserId();

                // Check if the username already exists
                String checkUserQuery = "SELECT id FROM users WHERE username = ?";
                try (PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery)) {
                    checkUserStatement.setString(1, username);
                    ResultSet resultSet = checkUserStatement.executeQuery();

                    if (resultSet.next()) {
                        // User with the username already exists
                        return "{ \"status\": \"User with this username already exists\" }";
                    }
                }

                // Insert a new user with the generated user ID
                String insertUserQuery = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";
                try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery)) {
                    insertUserStatement.setInt(1, userId);
                    insertUserStatement.setString(2, username);
                    insertUserStatement.setString(3, password);
                    insertUserStatement.executeUpdate();
                }

                return "{ \"status\": \"User registered successfully\", \"userId\": \"" + userId + "\" }";
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "{ \"status\": \"Failed to register user. Error: " + e.getMessage() + "\" }";
        }
    }

    
    @POST
    @Path("/loginUser")
    @Produces(MediaType.APPLICATION_JSON)
    public String loginUser(@FormParam("username") String username, @FormParam("password") String password,
                        @Context HttpServletRequest request) {
        try {
            // Connect to SQLite database
            Connection connection = DriverManager.getConnection(DB_PATH);

            // Check if the username and password match a user in the database
            String loginUserQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement loginUserStatement = connection.prepareStatement(loginUserQuery)) {
                loginUserStatement.setString(1, username);
                loginUserStatement.setString(2, password);
                ResultSet resultSet = loginUserStatement.executeQuery();

                if (resultSet.next()) {
                    // Login successful
                    int userId = resultSet.getInt("id");
                    String loggedInUsername = resultSet.getString("username");
                    String loggedInPassword = resultSet.getString("password");

                    // Set the user information in the session
                    HttpSession session = request.getSession(true);
                    session.setAttribute("loggedInUser", new User(userId, loggedInUsername, loggedInPassword));

                    connection.close();
                    return "{ \"status\": \"success\", \"userId\": " + userId + " }";
                } else {
                    // Login failed
                    connection.close();
                    return "{ \"status\": \"Invalid username or password\" }";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{ \"status\": \"Failed to log in. Error: " + e.getMessage() + "\" }";
        }
    }

    @GET
    @Path("/getLoggedInUser")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLoggedInUser(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            User loggedInUser = (User) session.getAttribute("loggedInUser");

            if (loggedInUser != null) {
                // Return user information
                return "{ \"status\": \"success\", \"userId\": " + loggedInUser.getId() +
                       ", \"username\": \"" + loggedInUser.getUsername() + "\" }";
            }
        }

        return "{ \"status\": \"error\", \"message\": \"User not logged in\" }";
    }

    @GET
    @Path("/generateUserId")
    public int generateUserId() {
        try {
            // Generate a random 5-digit number locally
            Random random = new Random();
            int generatedNumber = 10000 + random.nextInt(90000);

            return generatedNumber;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Or throw an exception
        }
    }
    
    @GET
    @Path("/getUserSpecificData")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUserSpecificData(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        List<User> userList = new ArrayList<>();

        if (session != null) {
            User loggedInUser = (User) session.getAttribute("loggedInUser");

            if (loggedInUser != null) {
                int userId = loggedInUser.getId();

                try {
                    // Connect to SQLite database
                    try ( Connection connection = DriverManager.getConnection(DB_PATH)) {
                        // Fetch user-specific data from the users table based on user ID
                        String getUserDataQuery = "SELECT * FROM users WHERE id = ?";
                        try ( PreparedStatement getUserDataStatement = connection.prepareStatement(getUserDataQuery)) {
                            getUserDataStatement.setInt(1, userId);
                            try ( ResultSet resultSet = getUserDataStatement.executeQuery()) {
                                while (resultSet.next()) {
                                    // Create a User object and add it to the list
                                    String username = resultSet.getString("username");
                                    String password = resultSet.getString("password");
                                    int hours = resultSet.getInt("hours");

                                    User userData = new User(userId, username, password, hours);
                                    userList.add(userData);
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return userList;
    }

    
    @POST
    @Path("/assignHours")
    @Produces(MediaType.APPLICATION_JSON)
    public String assignHours(@FormParam("userId") int userId, @FormParam("hours") int hours) {
        try {
            // Connect to SQLite database
            try ( Connection connection = DriverManager.getConnection(DB_PATH)) {
                // Check if the user with the given ID exists
                String checkUserQuery = "SELECT id FROM users WHERE id = ?";
                try ( PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery)) {
                    checkUserStatement.setInt(1, userId);
                    ResultSet resultSet = checkUserStatement.executeQuery();

                    if (resultSet.next()) {
                        // User with the given ID exists, update their hours
                        String assignHoursQuery = "UPDATE users SET hours = ? WHERE id = ?";
                        try ( PreparedStatement assignHoursStatement = connection.prepareStatement(assignHoursQuery)) {
                            assignHoursStatement.setInt(1, hours);
                            assignHoursStatement.setInt(2, userId);
                            assignHoursStatement.executeUpdate();
                        }

                        return "{ \"status\": \"success\" }";
                    } else {
                        return "{ \"status\": \"error\", \"message\": \"User not found\" }";
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{ \"status\": \"error\", \"message\": \"Failed to assign hours. Error: " + e.getMessage() + "\" }";
        }
    }

    
    @PreDestroy
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}





