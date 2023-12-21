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

    public User() {
        // Default constructor
    }

    public User(int id, String username, String password, int hours) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.hours = hours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}
