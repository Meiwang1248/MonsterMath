package edu.neu.madcourse.monstermath.Model;

public class Score {
    private String mark;
    private String username;

    public Score(String mark, String username) {
        this.mark = mark;
        this.username = username;
    }

    public Score() {

    }

    public String getMark() {
        return mark;
    }

    public String getUsername() {
        return username;
    }
}
