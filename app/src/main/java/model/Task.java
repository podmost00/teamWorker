package model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Task implements Serializable {
    private int id;
    private String name;
    private String description;
    private Date createTime;
    private Date dueTime;
    private Date lastEditTime;
    private Date startTime;
    private Date endTime;
    private String priority;
    private String stage;
    private String type;
    private boolean overdue;
    private User creator;
    private User assignee;
    private Project project;

    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
    SimpleDateFormat format2 = new SimpleDateFormat("dd.MM.yyyy, HH:mm");

    public Task(JSONObject jsonObject) {
        try {
            Log.d("ZAPROS", jsonObject.toString());
            this.id = jsonObject.getInt("id");
            this.name = jsonObject.getString("name");
            this.dueTime = format2.parse(jsonObject.getString("dueTime"));
            this.stage = jsonObject.getString("stage");
            this.description = jsonObject.getString("description");
            this.createTime = format1.parse(jsonObject.getString("createTime"));

                if (jsonObject.getString("lastEditTime").equals("")) {
                    this.lastEditTime = null;
                } else {
                    this.lastEditTime = format1.parse(jsonObject.getString("lastEditTime"));
                }

                if (jsonObject.getString("startTime").equals("")) {
                    this.startTime = null;
                } else {
                    this.startTime = format1.parse(jsonObject.getString("startTime"));
                }

            this.priority = jsonObject.getString("priority");
            this.type = jsonObject.getString("type");

            JSONObject projectObject = jsonObject.getJSONObject("project");
            this.project = new Project(projectObject.getString("name"));

            JSONObject assigneeObject = jsonObject.getJSONObject("assignee");
            String assigneeName = assigneeObject.getString("name");
            String assigneeSurname =  assigneeObject.getString("surname");
            this.assignee = new User(assigneeName, assigneeSurname);

            JSONObject creatorObject = jsonObject.getJSONObject("creator");
            String creatorName = creatorObject.getString("name");
            String creatorSurname =  creatorObject.getString("surname");
            this.creator = new User(creatorName, creatorSurname);

                if(Objects.equals(getStage(), "RELEASED")){
                    this.endTime = format1.parse(jsonObject.getString("endTime"));
                    this.overdue = dueTime.getTime() <= endTime.getTime();
                } else {
                    this.endTime = null;
                }






        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public String getCreator() {
        return creator.getName() + " " + creator.getSurname();
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getAssignee() {

    return assignee.getName() + " " + assignee.getSurname();
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            jsonObject.put("dueDate", dueTime);
            jsonObject.put("stage", stage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
