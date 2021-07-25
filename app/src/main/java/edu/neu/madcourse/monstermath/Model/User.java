package edu.neu.madcourse.monstermath.Model;

import java.util.HashMap;

public class User {
    private String id;
    private String username;
    private String token;
    public int numOfGamesPlayed;

    public User(String id, String username, String token) {
        this.id = id;
        this.username = username;
        this.token = token;
        numOfGamesPlayed = 0;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getNumOfGamesPlayed() {
        return numOfGamesPlayed;
    }

    public String getToken() {
        return token;
    }
}
