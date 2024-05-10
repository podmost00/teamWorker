package model;

import java.util.List;

public class Statistics {
    private String username;
    private String name;
    private String surname;
    private List<Position> position;
    private double percentageOnTime;
    private int totalCompletedTasks;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<Position> getPosition() {
        return position;
    }

    public void setPosition(List<Position> position) {
        this.position = position;
    }

    public double getPercentageOnTime() {
        return percentageOnTime;
    }

    public void setPercentageOnTime(double percentageOnTime) {
        this.percentageOnTime = percentageOnTime;
    }

    public int getTotalCompletedTasks() {
        return totalCompletedTasks;
    }

    public void setTotalCompletedTasks(int totalCompletedTasks) {
        this.totalCompletedTasks = totalCompletedTasks;
    }
}

