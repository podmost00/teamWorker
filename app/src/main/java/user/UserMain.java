package user;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;
import com.example.teamworker.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import auth.volleyAPI.AuthCallback;
import auth.volleyAPI.VolleyService;
import model.User;

public class UserMain extends AppCompatActivity {

    private LineChart tasksPerMonthChart;
    private PieChart taskCompletionStagesChart;
    private BarChart taskTypesChart;
    private VolleyService volleyService;
    private final String baseUrl = "http://192.168.0.108:8080/api/v1/tasks";
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        // Налаштування Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Знаходимо елементи інтерфейсу
        tasksPerMonthChart = findViewById(R.id.tasksPerMonthChart);
        taskCompletionStagesChart = findViewById(R.id.taskCompletionStagesChart);
        taskTypesChart = findViewById(R.id.taskTypesChart);

        volleyService = new VolleyService(this);
//        User user = (User) getIntent().getSerializableExtra("USER_OBJECT");
//        id = user.getId();
        setupCharts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void setupCharts() {
        setupLineChart();
        setupPieChart();
        setupBarChart();
        loadDataForCharts();
    }

    private void setupLineChart() {
        tasksPerMonthChart.getDescription().setEnabled(false);
        tasksPerMonthChart.getXAxis().setLabelRotationAngle(45);
        tasksPerMonthChart.getAxisLeft().setAxisMinimum(0);
    }

    private void setupPieChart() {
        taskCompletionStagesChart.setUsePercentValues(true);
        taskCompletionStagesChart.getDescription().setEnabled(false);
        taskCompletionStagesChart.setDrawHoleEnabled(true);
        taskCompletionStagesChart.setHoleColor(Color.WHITE);
        taskCompletionStagesChart.setTransparentCircleColor(Color.WHITE);
        taskCompletionStagesChart.setTransparentCircleAlpha(110);
        taskCompletionStagesChart.setHoleRadius(58f);
        taskCompletionStagesChart.setTransparentCircleRadius(61f);
        taskCompletionStagesChart.setDrawCenterText(true);
    }

    private void setupBarChart() {
        taskTypesChart.getDescription().setEnabled(false);
        taskTypesChart.getAxisLeft().setAxisMinimum(0);
    }

    private void loadDataForCharts() {
        getStatsMonths();
        getStatsType();
        getStatsStage();
    }

    private void getStatsMonths() {
        String url = baseUrl + "/get/stats/months/";
        volleyService.makeGetRequest(url, new AuthCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                List<Entry> entries = new ArrayList<>();
                try {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject monthData = data.getJSONObject(i);
                        String monthName = monthData.getString("name");
                        int taskCount = monthData.getInt("number");
                        entries.add(new Entry(i, taskCount));
                    }
                    LineDataSet dataSet = new LineDataSet(entries, "Кількість завдань по місяцях");
                    LineData lineData = new LineData(dataSet);
                    tasksPerMonthChart.setData(lineData);
                    tasksPerMonthChart.invalidate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(UserMain.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getStatsType() {
        String url = baseUrl + "/get/stats/types/";
        volleyService.makeGetRequest(url, new AuthCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                List<BarEntry> entries = new ArrayList<>();
                try {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject typeData = data.getJSONObject(i);
                        String typeName = typeData.getString("name");
                        int taskCount = typeData.getInt("number");
                        entries.add(new BarEntry(i, taskCount));
                    }
                    BarDataSet dataSet = new BarDataSet(entries, "Типи завдань");
                    BarData barData = new BarData(dataSet);
                    taskTypesChart.setData(barData);
                    taskTypesChart.invalidate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(UserMain.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getStatsStage() {
        String url = baseUrl + "/get/stats/stages/";
        volleyService.makeGetRequest(url, new AuthCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                List<PieEntry> entries = new ArrayList<>();
                try {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject stageData = data.getJSONObject(i);
                        String stageName = stageData.getString("name");
                        int taskCount = stageData.getInt("number");
                        entries.add(new PieEntry(taskCount, stageName));
                    }
                    PieDataSet dataSet = new PieDataSet(entries, "Стадії завдань");
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    PieData pieData = new PieData(dataSet);
                    taskCompletionStagesChart.setData(pieData);
                    taskCompletionStagesChart.invalidate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(UserMain.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
