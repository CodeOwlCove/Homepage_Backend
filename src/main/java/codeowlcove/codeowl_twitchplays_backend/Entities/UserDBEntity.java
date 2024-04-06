package codeowlcove.codeowl_twitchplays_backend.Entities;

import jakarta.persistence.*;
import okhttp3.internal.platform.Platform;

@Entity
@Table(name = "users")
public class UserDBEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="UID")
    private Long userID;

    @Column(name="Username")
    private String username;
    @Column(name="Platform")
    private String platform;
    @Column(name="Points")
    private int points;

    public UserDBEntity() {
        this.username = null;
        this.points = -1;
        this.platform = null;
    }

    public UserDBEntity(String username, int points, String platform) {
        this.username = username;
        this.points = points;
        this.platform = platform;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}