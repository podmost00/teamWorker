package model;

import java.io.Serializable;
import java.util.List;

import model.Position;
import model.User;

public class Project implements Serializable {
    private int id;
    private String name;
    private String createTime;
    private String projectStage;
    private String projectType;
    private User manager;
    private List<Position> positions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getProjectStage() {
        return projectStage;
    }

    public void setProjectStage(String projectStage) {
        this.projectStage = projectStage;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
}
