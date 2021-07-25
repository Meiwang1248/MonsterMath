package edu.neu.madcourse.monstermath;

public class ScoreItem {
    private String username;
    private String score;

    public ScoreItem(String username, String score) {
        this.username = username;
        this.score = score;
    }

    public ScoreItem(){}

    public String getUsername() {
        return this.username;
    }

    public String getScore() {
        return this.score;
    }
}
