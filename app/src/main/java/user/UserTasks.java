package user;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.teamworker.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import callback.GetArrayCallback;
import services.TaskAdapter;
import callback.TaskMoveCallback;
import callback.TaskMoveListener;
import services.TaskParser;
import model.Task;
import services.VolleyService;

public class UserTasks extends AppCompatActivity {

    private RecyclerView createdRecyclerView;
    private RecyclerView inProgressRecyclerView;
    private RecyclerView onReviewRecyclerView;
    private RecyclerView releasedRecyclerView;

    private TaskAdapter createdAdapter;
    private TaskAdapter inProgressAdapter;
    private TaskAdapter onReviewAdapter;
    private TaskAdapter releasedAdapter;
    private VolleyService volleyService;
    private final String baseUrl = "http://192.168.0.108:8080/api/v1/tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tasks);
        volleyService = new VolleyService(this);

        createdRecyclerView = findViewById(R.id.created_recycler_view);
        inProgressRecyclerView = findViewById(R.id.in_progress_recycler_view);
        onReviewRecyclerView = findViewById(R.id.on_review_recycler_view);
        releasedRecyclerView = findViewById(R.id.released_recycler_view);

        createdAdapter = new TaskAdapter(new ArrayList<>(), task -> {});
        inProgressAdapter = new TaskAdapter(new ArrayList<>(), task -> {});
        onReviewAdapter = new TaskAdapter(new ArrayList<>(), task -> {});
        releasedAdapter = new TaskAdapter(new ArrayList<>(), task -> {});

        createdRecyclerView.setAdapter(createdAdapter);
        inProgressRecyclerView.setAdapter(inProgressAdapter);
        onReviewRecyclerView.setAdapter(onReviewAdapter);
        releasedRecyclerView.setAdapter(releasedAdapter);

        createdRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        inProgressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        onReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        releasedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new TaskMoveCallback(new TaskMoveListener() {
            @Override
            public boolean onTaskMove(int fromPosition, int toPosition) {
                // Логіка переносу завдання
                return true;
            }

            @Override
            public void onTaskSwiped(int position) {
                // Логіка свапу
            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(createdRecyclerView);
        touchHelper.attachToRecyclerView(inProgressRecyclerView);
        touchHelper.attachToRecyclerView(onReviewRecyclerView);
        touchHelper.attachToRecyclerView(releasedRecyclerView);

        loadTasks();
    }

    private void loadTasks() {
        String url = baseUrl + "/get/all/RELEASED";
        volleyService.makeGetArrayRequest(url, new GetArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) throws JSONException {
                try {
                    List<Task> tasks = TaskParser.parseTasks(response);
                    releasedAdapter.updateTasks(tasks);
                } catch (ParseException e) {
                    Toast.makeText(UserTasks.this, "Error parsing date: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(UserTasks.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTask(Task task) {
        // Логіка для запуску завдання
    }

    private void finishTask(Task task) {
        // Логіка для завершення завдання
    }
}
