package services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Task;
import model.User;
import model.Project;

public class TaskParser {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static List<Task> parseTasks(JSONArray response) throws JSONException, ParseException {
        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonTask = response.getJSONObject(i);
            Task task = new Task();

            task.setId(jsonTask.getInt("id"));
            task.setName(jsonTask.getString("name"));
            task.setDescription(jsonTask.getString("description"));
            task.setCreateTime(parseDate(jsonTask.getString("createTime")));
            task.setDueTime(parseDate(jsonTask.getString("dueTime")));
            task.setLastEditTime(parseDate(jsonTask.getString("lastEditTime")));
            task.setStartTime(parseDate(jsonTask.getString("startTime")));
            task.setEndTime(parseDate(jsonTask.getString("endTime")));
            task.setPriority(jsonTask.getString("priority"));
            task.setStage(jsonTask.getString("stage"));
            task.setType(jsonTask.getString("type"));
            task.setOverdue(jsonTask.getBoolean("overdue"));

            task.setAssignee(parseUser(jsonTask.getJSONObject("assignee")));
            task.setCreator(parseUser(jsonTask.getJSONObject("creator")));
            task.setProject(parseProject(jsonTask.getJSONObject("project")));

            tasks.add(task);
        }

        return tasks;
    }

    private static Date parseDate(String dateStr) throws ParseException {
        return dateFormat.parse(dateStr);
    }

    private static User parseUser(JSONObject jsonUser) throws JSONException, ParseException {
        User user = new User();
        user.setId(jsonUser.getInt("id"));
        user.setUsername(jsonUser.getString("username"));
        user.setName(jsonUser.getString("name"));
        user.setSurname(jsonUser.getString("surname"));

        return user;
    }

    private static Project parseProject(JSONObject jsonProject) throws JSONException, ParseException {
        Project project = new Project();
        project.setId(jsonProject.getInt("id"));
        project.setName(jsonProject.getString("name"));
        project.setCreateTime(parseDate(jsonProject.getString("createTime")).toString());
        project.setProjectStage(jsonProject.getString("projectStage"));
        project.setProjectType(jsonProject.getString("projectType"));
        return project;
    }
}
