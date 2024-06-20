package user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auth.login.Login;
import callback.GetArrayCallback;
import callback.GetStringCallback;
import services.TokenStorageService;
import services.VolleyService;

public class UserStats extends AppCompatActivity {

    private LineChart tasksPerMonthChart;
    private PieChart taskCompletionStagesChart;
    private BarChart taskTypesChart;
    private VolleyService volleyService;
    private final String baseUrl = "http://192.168.56.1:8080/api/v1/tasks";
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
        MenuItem tasksItem = menu.findItem(R.id.action_tasks);
        MenuItem statsItem = menu.findItem(R.id.action_statistics);

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
            loadSummaryData();
            loadDataForCharts();
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



    private void setupCharts() {
        setupLineChart();
        setupPieChart();
        setupBarChart();
        loadDataForCharts();
    }

    private void setupLineChart() {
        tasksPerMonthChart.getDescription().setEnabled(false);
        tasksPerMonthChart.setDrawGridBackground(false);
        tasksPerMonthChart.setTouchEnabled(true);
        tasksPerMonthChart.setDragEnabled(true);
        tasksPerMonthChart.setScaleEnabled(true);
        tasksPerMonthChart.setPinchZoom(true);

        XAxis xAxis = tasksPerMonthChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6, true);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = tasksPerMonthChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        tasksPerMonthChart.getAxisRight().setEnabled(false);
        tasksPerMonthChart.getRendererLeftYAxis().getPaintGrid().setColor(Color.BLACK);
        tasksPerMonthChart.getRendererLeftYAxis().getPaintAxisLine().setColor(Color.BLACK);
        tasksPerMonthChart.setScaleYEnabled(true);
        tasksPerMonthChart.setScaleXEnabled(true);
        tasksPerMonthChart.invalidate();
    }


    private void setupPieChart() {
        taskCompletionStagesChart.setUsePercentValues(true);
        taskCompletionStagesChart.getDescription().setEnabled(false);
        taskCompletionStagesChart.setDrawHoleEnabled(false);
        taskCompletionStagesChart.setHoleColor(Color.WHITE);
        taskCompletionStagesChart.setTransparentCircleColor(Color.WHITE);
        taskCompletionStagesChart.setTransparentCircleAlpha(110);
        taskCompletionStagesChart.setHoleRadius(58f);
        taskCompletionStagesChart.setTransparentCircleRadius(61f);
        taskCompletionStagesChart.setDrawCenterText(false);
        taskCompletionStagesChart.setEntryLabelColor(Color.BLACK);
        taskCompletionStagesChart.setEntryLabelTextSize(14f);
        taskCompletionStagesChart.setDrawEntryLabels(false);
        taskCompletionStagesChart.getLegend().setEnabled(true);
        taskCompletionStagesChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
        taskCompletionStagesChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        taskCompletionStagesChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        taskCompletionStagesChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        taskCompletionStagesChart.getLegend().setDrawInside(false);
        taskCompletionStagesChart.invalidate();
    }



    private void setupBarChart() {
        taskTypesChart.getDescription().setEnabled(false);
        taskTypesChart.setDrawGridBackground(false);
        taskTypesChart.setDrawBarShadow(false);
        taskTypesChart.setPinchZoom(true);
        taskTypesChart.setDrawValueAboveBar(true);
        taskTypesChart.setMaxVisibleValueCount(60);

        XAxis xAxis = taskTypesChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6, true);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = taskTypesChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularity(1f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        taskTypesChart.getAxisRight().setEnabled(false);

        Legend legend = taskTypesChart.getLegend();
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        taskTypesChart.invalidate();
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
                Toast.makeText(UserStats.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UserStats.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UserStats.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UserStats.this, "Помилка завантаження даних: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getStatsMonths() {
        String url = baseUrl + "/get/stats/months";
        volleyService.makeGetArrayRequest(url, new GetArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<Entry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject monthData = response.getJSONObject(i);
                        String month = monthData.getString("name");
                        int taskCount = monthData.getInt("number");
                        entries.add(new Entry(i, taskCount));
                        labels.add(month);
                    }
                    LineDataSet dataSet = new LineDataSet(entries, "");
                    dataSet.setColor(Color.rgb(63,81,181));
                    dataSet.setCircleColor(Color.rgb(63,81,181));
                    dataSet.setLineWidth(2f);
                    dataSet.setCircleRadius(4f);
                    dataSet.setValueTextSize(10f);
                    dataSet.setDrawCircleHole(false);

                    LineData lineData = new LineData(dataSet);
                    tasksPerMonthChart.setData(lineData);

                    tasksPerMonthChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

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

                    int[] colors = {
                            Color.rgb(63, 81, 181),
                            Color.rgb(255, 153, 0),
                            Color.rgb(87, 169, 83),
                            Color.rgb(220, 57, 18)
                    };

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(colors);

                    PieData pieData = new PieData(dataSet);
                    pieData.setValueTextSize(14f);
                    pieData.setValueTextColor(Color.WHITE);
                    pieData.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.format("%.0f%%", value);
                        }
                    });

                    taskCompletionStagesChart.setData(pieData);
                    taskCompletionStagesChart.invalidate();

                    taskCompletionStagesChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        @Override
                        public void onValueSelected(Entry e, Highlight h) {
                            if (e instanceof PieEntry) {
                                PieEntry pe = (PieEntry) e;
                                Toast.makeText(UserStats.this, pe.getLabel() + ": " + (int) pe.getValue(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected() {}
                    });

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
                final Map<Integer, String> indexToNameMap = new HashMap<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject typeData = response.getJSONObject(i);
                        String typeName = typeData.getString("name");
                        int taskCount = typeData.getInt("number");
                        labels.add(typeName);
                        entries.add(new BarEntry(i, taskCount));
                        indexToNameMap.put(i, typeName);
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "Типи завдань");


                    int[] colors = {
                            Color.rgb(63, 81, 181),
                            Color.rgb(255, 153, 0),
                            Color.rgb(87, 169, 83),
                            Color.rgb(220, 57, 18),
                            Color.rgb(156, 39, 176),
                            Color.rgb(0, 150, 136)
                    };
                    dataSet.setColors(colors);
                    dataSet.setValueTextSize(10f);
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.valueOf((int) value);
                        }
                    });

                    BarData barData = new BarData(dataSet);
                    barData.setBarWidth(0.9f);

                    taskTypesChart.setData(barData);
                    taskTypesChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                    taskTypesChart.invalidate();

                    taskTypesChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                        @Override
                        public void onValueSelected(Entry e, Highlight h) {
                            if (e instanceof BarEntry) {
                                int index = (int) e.getX();
                                String typeName = indexToNameMap.get(index);
                                if (typeName != null) {
                                    Toast.makeText(getApplicationContext(), typeName, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Невідомий тип", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected() {
                            // Do nothing
                        }
                    });
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
