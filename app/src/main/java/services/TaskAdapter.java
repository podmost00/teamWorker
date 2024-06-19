package services;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.teamworker.R;

import org.json.JSONArray;

import java.util.List;
import java.util.Objects;

import callback.GetArrayCallback;
import model.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private OnTaskClickListener listener;
    private Context context;
    private AlertDialog changeStatusDialog;
    private VolleyService volleyService;
    private final String baseGETUrl = "http://192.168.56.1:8080/api/v1/tasks/get/all/";
    private final String basePUTUrl = "http://192.168.56.1:8080/api/v1/tasks";

    public TaskAdapter(Context context, List<Task> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
        this.context = context;
        this.volleyService = new VolleyService(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task, listener);

        holder.changeStatusButton.setOnClickListener(v -> {
            showChangeStatusDialog(task);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public Task getTask(int position) {
        return tasks.get(position);
    }

    public void removeTask(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
    }

    public void moveTask(int fromPosition, int toPosition) {
        Task task = tasks.remove(fromPosition);
        tasks.add(toPosition, task);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void showChangeStatusDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_change_status, null);
        builder.setView(view);

        RadioGroup statusRadioGroup = view.findViewById(R.id.status_radio_group);
        Button changeStatusButton = view.findViewById(R.id.button_change_status);
        ImageView closeButton = view.findViewById(R.id.button_close);

        closeButton.setOnClickListener(v -> changeStatusDialog.dismiss());

        changeStatusButton.setOnClickListener(v -> {
            int selectedId = statusRadioGroup.getCheckedRadioButtonId();
            String newStatus = "";
            if (selectedId == R.id.radio_new) {
                newStatus = "CREATED";
            } else if (selectedId == R.id.radio_in_progress) {
                newStatus = "IN_PROGRESS";
            } else if (selectedId == R.id.radio_on_review) {
                newStatus = "ON_REVIEW";
            } else if (selectedId == R.id.radio_released) {
                newStatus = "RELEASED";
            }

            updateTaskStage(task, newStatus);
            changeStatusDialog.dismiss();
        });

        changeStatusDialog = builder.create();
        changeStatusDialog.show();
    }

    private void updateTaskStage(Task task, String newStage) {
        task.setStage(newStage);
        String url = basePUTUrl + "/update/" + task.getId() + "/" + newStage;
        volleyService.makePUTObjectRequest(url, task.toJson(), new GetArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {

            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        private TextView taskName;
        private TextView taskPriority;
        private TextView taskAssignedTo;
        private TextView taskDueTime;
        private TextView taskProject;
        private ImageView changeStatusButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_title);
            taskPriority = itemView.findViewById(R.id.task_priority);
            taskAssignedTo = itemView.findViewById(R.id.task_assigned_to);
            taskDueTime = itemView.findViewById(R.id.task_due_date);
            taskProject = itemView.findViewById(R.id.task_project_title);
            changeStatusButton = itemView.findViewById(R.id.button_options);
            changeStatusButton.setOnClickListener(v -> {
                showChangeStatusDialog(tasks.get(getAdapterPosition()));
            });
        }

        public void bind(Task task, OnTaskClickListener listener) {

            taskName.setText(task.getName());
            if (Objects.equals(task.getPriority(), "HIGH")){
                taskPriority.setText("Високий");
                taskPriority.setTextColor(Color.rgb(0,0,0));
                taskPriority.setBackgroundColor(Color.rgb(255,99,71));
            } else if (Objects.equals(task.getPriority(), "MEDIUM")){
                taskPriority.setText("Середній");
                taskPriority.setTextColor(Color.rgb(0,0,0));
                taskPriority.setBackgroundColor(Color.rgb(255,191,102));
            } else {
                taskPriority.setText("Низький");
                taskPriority.setTextColor(Color.rgb(0,0,0));
                taskPriority.setBackgroundColor(Color.rgb(122,227,157));
            }
            taskProject.setText(task.getProject().getName());
            taskAssignedTo.setText("Для: " + task.getAssignee());
            taskDueTime.setText(task.getDueTime().toLocaleString());
            itemView.setOnClickListener(v -> listener.onTaskClick(task));
        }
    }

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }
}
