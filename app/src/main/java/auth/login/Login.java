package auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.teamworker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import auth.register.Register;
import callback.AuthCallback;
import services.TokenStorageService;
import admin.AdminMain;
import services.VolleyService;
import manager.ManagerMain;
import model.Position;
import model.Project;
import model.Role;
import model.User;
import user.UserStats;

public class Login extends AppCompatActivity {

    private TokenStorageService tokenStorage;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView regLink;
    private VolleyService volleyService;
    private static final String AUTH_API = "http://192.168.56.1:8080/api/v1/auth/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.login_login);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_loginButton);
        regLink = findViewById(R.id.login_regLink);
        volleyService = new VolleyService(this);
        tokenStorage = new TokenStorageService(this);

        regLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                JSONObject credentials = new JSONObject();
                try {
                    credentials.put("username", username);
                    credentials.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String loginUrl = AUTH_API + "login";

                volleyService.login(loginUrl, credentials, new AuthCallback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        try {
                            String token = response.getString("token");
                            String username = response.getString("username");
                            tokenStorage.saveToken(token);
                            String userUrl = "http://192.168.56.1:8080/api/v1/users/get/" + username;
                            JsonObjectRequest userRequest = new JsonObjectRequest(Request.Method.GET, userUrl, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject userResponse) {
                                            Log.d("RESPONSE", userResponse.toString());

                                            try {
                                                User user = new User();
                                                user.setId(userResponse.getInt("id"));
                                                user.setUsername(userResponse.getString("username"));
                                                user.setName(userResponse.getString("name"));
                                                user.setSurname(userResponse.getString("surname"));

                                                JSONArray positionsArray = userResponse.getJSONArray("position");
                                                List<Position> positions = new ArrayList<>();

                                                for (int i = 0; i < positionsArray.length(); i++) {
                                                    JSONObject positionObject = positionsArray.getJSONObject(i);

                                                    Position position = new Position();
                                                    position.setId(positionObject.getInt("id"));
                                                    position.setName(positionObject.getString("name"));

                                                    JSONObject projectObject = positionObject.getJSONObject("project");
                                                    Project project = new Project();
                                                    project.setId(projectObject.getInt("id"));
                                                    project.setName(projectObject.getString("name"));
                                                    project.setCreateTime(projectObject.getString("createTime"));
                                                    project.setProjectStage(projectObject.getString("projectStage"));
                                                    project.setProjectType(projectObject.getString("projectType"));

                                                    position.setProject(project);
                                                    positions.add(position);
                                                }
                                                user.setPosition(positions);

                                                JSONArray rolesArray = userResponse.getJSONArray("roles");
                                                List<Role> roles = new ArrayList<>();

                                                for (int i = 0; i < rolesArray.length(); i++) {
                                                    JSONObject roleObject = rolesArray.getJSONObject(i);

                                                    Role role = new Role();
                                                    role.setId(roleObject.getInt("id"));
                                                    role.setName(roleObject.getString("name"));

                                                    roles.add(role);
                                                }
                                                user.setRoles(roles);

                                                Log.d("USER_OBJECT", user.toString());

                                                if (roles.size() > 0) {
                                                    String userRole = roles.get(0).getName();
                                                    Intent intent;
                                                    if (userRole.equals("ROLE_ADMIN")) {
                                                        intent = new Intent(Login.this, AdminMain.class);
                                                    } else if (userRole.equals("ROLE_MANAGER")) {
                                                        intent = new Intent(Login.this, ManagerMain.class);
                                                    } else {
                                                        intent = new Intent(Login.this, UserStats.class);
                                                    }
                                                    intent.putExtra("USER_OBJECT", user);
                                                    Toast.makeText(Login.this, "Вітаємо, " + user.getName(), Toast.LENGTH_SHORT).show();
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(Login.this, "Невідома роль", Toast.LENGTH_SHORT).show();
                                                }

                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(Login.this, "Помилка отримання даних користувача: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Authorization", "Bearer " + token);
                                    return headers;
                                }
                            };

                            Volley.newRequestQueue(Login.this).add(userRequest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Login.this, "Помилка сервера", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(Login.this, "Невірний логін або пароль", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
