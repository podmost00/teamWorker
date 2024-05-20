package user;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import auth.volleyAPI.GetArrayCallback;
import auth.volleyAPI.GetStringCallback;
import auth.volleyAPI.VolleyService;

public class UserMain extends AppCompatActivity {

    private LineChart tasksPerMonthChart;
    private PieChart taskCompletionStagesChart;
    private BarChart taskTypesChart;
    private VolleyService volleyService;
    private final String baseUrl = "http://192.168.0.108:8080/api/v1/tasks";
    private int id;

    private TextView totalTasks, avgDuration, mostTasksMonth, shortestDurationTask, onTimeTasksPercentage;
    private ProgressBar onTimeTasksProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tasksPerMonthChart = findViewById(R.id.tasksPerMonthChart);
        taskCompletionStagesChart = findViewById(R.id.taskCompletionStagesChart);
        taskTypesChart = findViewById(R.id.taskTypesChart);

        totalTasks = findViewById(R.id.totalTasks);
        avgDuration = findViewById(R.id.avgDuration);
        mostTasksMonth = findViewById(R.id.mostTasksMonth);
        onTimeTasksProgress = findViewById(R.id.onTimeTasksProgress);
        onTimeTasksPercentage = findViewById(R.id.onTimeTasksPercentage);

        volleyService = new VolleyService(this);

        setupCharts();
        loadSummaryData();
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

    private void loadSummaryData() {
        getAvgDuration();
        getMostTasksMonth();
        getOnTimeTasks();
        getAllTasks();
    }

    private void getAllTasks() {
        String url = baseUrl + "/get/all/RELEASED";
        volleyService.makeGetArrayRequest(url, new GetArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                totalTasks.setText("Всього зроблено: " + response.length());
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(UserMain.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getAvgDuration() {
        String url = baseUrl + "/get/stats/average/time";
        volleyService.makeGetStringRequest(url, new GetStringCallback() {
            @Override
            public void onSuccess(String response) {
                String time = response.substring(13, 21);
                avgDuration.setText("Середня тривалість виконання: " + time);
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(UserMain.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMostTasksMonth() {
        String url = baseUrl + "/get/stats/best/month";
        volleyService.makeGetStringRequest(url, new GetStringCallback() {
            @Override
            public void onSuccess(String response) {
                mostTasksMonth.setText("Найбільше за місяць: " + response);
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(UserMain.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void getOnTimeTasks() {
        String url = baseUrl + "/get/stats/ontime";
        volleyService.makeGetStringRequest(url, new GetStringCallback() {
            @Override
            public void onSuccess(String response) {
                onTimeTasksProgress.setProgress(Integer.parseInt(response));
                onTimeTasksPercentage.setText(response + "% виконано вчасно");
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(UserMain.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getStatsMonths() {
        String url = baseUrl + "/get/stats/months";
        volleyService.makeGetArrayRequest(url, new GetArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<Entry> entries = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject monthData = response.getJSONObject(i);
                        String month = monthData.getString("name");
                        int taskCount = monthData.getInt("number");
                        entries.add(new Entry(i, taskCount));
                    }
                    LineDataSet dataSet = new LineDataSet(entries, "Завдання по місяцях");
                    dataSet.setColor(Color.BLUE);
                    dataSet.setValueTextColor(Color.BLACK);
                    LineData lineData = new LineData(dataSet);
                    tasksPerMonthChart.setData(lineData);
                    tasksPerMonthChart.invalidate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.d("ERROR","ERROR");
                error.printStackTrace();
            }
        });
    }

    private void getStatsStage() {
        String url = baseUrl + "/get/stats/stages/";
        volleyService.makeGetArrayRequest(url, new GetArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<PieEntry> entries = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject stageData = response.getJSONObject(i);
                        String stageName = stageData.getString("name");
                        int taskCount = stageData.getInt("number");
                        entries.add(new PieEntry(taskCount, stageName));
                    }
                    PieDataSet dataSet = new PieDataSet(entries, "Етапи виконання завдань");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    PieData pieData = new PieData(dataSet);
                    taskCompletionStagesChart.setData(pieData);
                    taskCompletionStagesChart.invalidate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    private void getStatsType() {
        String url = baseUrl + "/get/stats/types/";
        volleyService.makeGetArrayRequest(url, new GetArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject typeData = response.getJSONObject(i);
                        String typeName = typeData.getString("name");
                        int taskCount = typeData.getInt("number");
                        entries.add(new BarEntry(i, taskCount));
                        labels.add(typeName);
                    }
                    BarDataSet dataSet = new BarDataSet(entries, "Типи завдань");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    BarData barData = new BarData(dataSet);
                    taskTypesChart.setData(barData);

                    taskTypesChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                    taskTypesChart.invalidate(); // refresh chart
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
            }
        });
    }
}
