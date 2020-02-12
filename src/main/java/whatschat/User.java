package whatschat;

import java.util.Date;

/**
 * User
 */
public class User {
    String userName;
    String description = "";
    String iconLocation;
    Date lastSeen = new Date();

    public User(String userName) {
        this.userName = userName;
    }

    public User(String userName, String description) {
        this.userName = userName;
        this.description = description;
    }

    public User(String userName, String description, Date lastSeen) {
        this.userName = userName;
        this.description = description;
        this.lastSeen = lastSeen;
    }

    public User(String userName, String description, String iconLocation) {
        this.userName = userName;
        this.description = description;
        this.iconLocation = iconLocation;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the iconLocation
     */
    public String getIconLocation() {
        return iconLocation;
    }

    /**
     * @param iconLocation the iconLocation to set
     */
    public void setIconLocation(String iconLocation) {
        this.iconLocation = iconLocation;
    }
}