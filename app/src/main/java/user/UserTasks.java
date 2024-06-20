package user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.teamworker.R;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import auth.login.Login;
import callback.GetArrayCallback;
import model.Task;
import services.TaskAdapter;
import services.TokenStorageService;
import services.VolleyService;


public class UserTasks extends AppCompatActivity {

    private RecyclerView createdRecyclerView, inProgressRecyclerView, onReviewRecyclerView, releasedRecyclerView;
    private TaskAdapter createdAdapter, inProgressAdapter, onReviewAdapter, releasedAdapter;
    private VolleyService volleyService;
    private final String baseUrl = "http://192.168.56.1:8080/api/v1/tasks/get/all/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tasks);
        createdRecyclerView = findViewById(R.id.created_recycler_view);
        inProgressRecyclerView = findViewById(R.id.in_progress_recycler_view);
        onReviewRecyclerView = findViewById(R.id.on_review_recycler_view);
        releasedRecyclerView = findViewById(R.id.released_recycler_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        volleyService = new VolleyService(this);

        createdAdapter = new TaskAdapter(this, new ArrayList<>(), this::onTaskClick);
        inProgressAdapter = new TaskAdapter(this, new ArrayList<>(), this::onTaskClick);
        onReviewAdapter = new TaskAdapter(this, new ArrayList<>(), this::onTaskClick);
        releasedAdapter = new TaskAdapter(this, new ArrayList<>(), this::onTaskClick);

        createdRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        inProgressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        onReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        releasedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        createdRecyclerView.setAdapter(createdAdapter);
        inProgressRecyclerView.setAdapter(inProgressAdapter);
        onReviewRecyclerView.setAdapter(onReviewAdapter);
        releasedRecyclerView.setAdapter(releasedAdapter);

        loadTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem tasksItem = menu.findItem(R.id.action_tasks);
        MenuItem statsItem = menu.findItem(R.id.action_statistics);

        // Деактивація кнопки поточної активності
        tasksItem.setEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.action_tasks) {
            intent = new Intent(this, UserTasks.class);
        } else if (id == R.id.action_statistics) {
            intent = new Intent(this, UserStats.class);
        } else if (id == R.id.action_logout) {
            logoutAndExit();
            return true;
        } else if (id == R.id.action_refresh) {
            loadTasks();
            return true;
        }

        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutAndExit() {
        TokenStorageService tokenStorage = new TokenStorageService(this);
        tokenStorage.logOut();

        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void onTaskClick(Task task) {
        // Обробка натискання на завдання
    }

    public void loadTasks() {
        loadTasks("CREATED", createdAdapter);
        loadTasks("IN_PROGRESS", inProgressAdapter);
        loadTasks("ON_REVIEW", onReviewAdapter);
        loadTasks("RELEASED", releasedAdapter);
    }

    private void loadTasks(String stage, TaskAdapter adapter) {
        String url = baseUrl + stage;
        volleyService.makeGetArrayRequest(url, new GetArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<Task> tasks = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject taskJson = response.optJSONObject(i);
                    if (taskJson != null) {
                        Task task = new Task(taskJson);
                        tasks.add(task);
                    }
                }
                adapter.updateTasks(tasks);
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(UserTasks.this, "Помилка завантаження завдань: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
