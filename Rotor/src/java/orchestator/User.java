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
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
    private int id;
    private String username;
    private String password;
    private int hours;

    // Existing constructor
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        // Set a default value for hours or modify as needed
        this.hours = 0;
    }

    // New constructor with four parameters
    public User(int id, String username, String password, int hours) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.hours = hours;
    }

    

    // Empty constructor
    public User() {
        // Default constructor
    }

    // Getter and setter methods...

    // Getter and setter methods for 'id'
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and setter methods for 'username'
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and setter methods for 'password'
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and setter methods for 'hours'
    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}


