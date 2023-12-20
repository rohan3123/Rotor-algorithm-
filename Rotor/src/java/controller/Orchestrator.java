/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author Rohan
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/orchestrator")
public class Orchestrator {

    private static final String DB_PATH = "jdbc:sqlite:Employee.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/createDatabase")
    @Produces(MediaType.APPLICATION_JSON)
    public String createDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            System.out.println("Connected to database: " + DB_PATH);

            String createEmployeeTableQuery = "CREATE TABLE IF NOT EXISTS employee ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT NOT NULL,"
                    + "password TEXT NOT NULL);";

            String createRotorTableQuery = "CREATE TABLE IF NOT EXISTS rotor ("
                    + "employee_id INTEGER NOT NULL,"
                    + "name TEXT NOT NULL,"
                    + "hours TEXT NOT NULL,"
                    + "FOREIGN KEY (employee_id) REFERENCES employee(id));";

            try (PreparedStatement createEmployeeTableStatement = connection.prepareStatement(createEmployeeTableQuery);
                 PreparedStatement createRotorTableStatement = connection.prepareStatement(createRotorTableQuery)) {
                createEmployeeTableStatement.execute();
                createRotorTableStatement.execute();
            }

            return "{ \"status\": \"Database created successfully\" }";
        } catch (SQLException e) {
            e.printStackTrace();
            return "{ \"status\": \"Failed to create Database. Error: " + e.getMessage() + "\" }";
        }
    }

    @GET
    @Path("/checkDatabaseConnection")
    @Produces(MediaType.APPLICATION_JSON)
    public String checkDatabaseConnection() {
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            System.out.println("Connected to database: " + DB_PATH);
            return "{ \"status\": \"Connected to Database\" }";
        } catch (SQLException e) {
            e.printStackTrace();
            return "{ \"status\": \"Failed to connect to Database. Error: " + e.getMessage() + "\" }";
        }
    }

    @GET
    @Path("/addUser")
    @Produces(MediaType.APPLICATION_JSON)
    public String addUser() {
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            String checkUserQuery = "SELECT id FROM employee WHERE id = ?";
            try (PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery)) {
                checkUserStatement.setInt(1, 12345);
                ResultSet resultSet = checkUserStatement.executeQuery();

                if (resultSet.next()) {
                    return "{ \"status\": \"Admin user already exists\" }";
                }

                String insertUserQuery = "INSERT INTO employee (id, username, password) VALUES (?, ?, ?)";
                try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery)) {
                    insertUserStatement.setInt(1, 12345);
                    insertUserStatement.setString(2, "admin");
                    insertUserStatement.setString(3, "admin");
                    insertUserStatement.executeUpdate();
                }

                return "{ \"status\": \"User added successfully\" }";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{ \"status\": \"Failed to add user. Error: " + e.getMessage() + "\" }";
        }
    }

    @GET
    @Path("/getAllUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Employee> getAllUsers() {
        List<Employee> userList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            String getAllUsersQuery = "SELECT * FROM employee";
            try (PreparedStatement getAllUsersStatement = connection.prepareStatement(getAllUsersQuery)) {
                ResultSet resultSet = getAllUsersStatement.executeQuery();

                while (resultSet.next()) {
                    int employeeId = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");

                    Employee user = new Employee(employeeId, username, password);
                    userList.add(user);
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
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            int userId = generateUserId();

            String checkUserQuery = "SELECT id FROM employee WHERE username = ?";
            try (PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery)) {
                checkUserStatement.setString(1, username);
                ResultSet resultSet = checkUserStatement.executeQuery();

                if (resultSet.next()) {
                    return "{ \"status\": \"User with this username already exists\" }";
                }

                String insertUserQuery = "INSERT INTO employee (id, username, password) VALUES (?, ?, ?)";
                try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery)) {
                    insertUserStatement.setInt(1, userId);
                    insertUserStatement.setString(2, username);
                    insertUserStatement.setString(3, password);
                    insertUserStatement.executeUpdate();
                }

                return "{ \"status\": \"User registered successfully\", \"userId\": \"" + userId + "\" }";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{ \"status\": \"Failed to register user. Error: " + e.getMessage() + "\" }";
        }
    }

    @POST
    @Path("/loginUser")
    @Produces(MediaType.APPLICATION_JSON)
    public String loginUser(@FormParam("username") String username, @FormParam("password") String password,
                            @Context HttpServletRequest request) {
        try (Connection connection = DriverManager.getConnection(DB_PATH)) {
            String loginUserQuery = "SELECT * FROM employee WHERE username = ? AND password = ?";
            try (PreparedStatement loginUserStatement = connection.prepareStatement(loginUserQuery)) {
                loginUserStatement.setString(1, username);
                loginUserStatement.setString(2, password);
                ResultSet resultSet = loginUserStatement.executeQuery();

                if (resultSet.next()) {
                    int employeeId = resultSet.getInt("id");
                    String loggedInUsername = resultSet.getString("username");
                    String loggedInPassword = resultSet.getString("password");

                    HttpSession session = request.getSession(true);
                    session.setAttribute("loggedInUser", new Employee(employeeId, loggedInUsername, loggedInPassword));

                    return "{ \"status\": \"success\" }";
                } else {
                    return "{ \"status\": \"Invalid username or password\" }";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{ \"status\": \"Failed to log in. Error: " + e.getMessage() + "\" }";
        }
    }

    @GET
    @Path("/generateUserId")
    public int generateUserId() {
        Random random = new Random();
        return 10000 + random.nextInt(90000);
    }
}




