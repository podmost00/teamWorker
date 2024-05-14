package auth.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.Map;
import auth.register.Register;
import auth.volleyAPI.AuthCallback;
import auth.volleyAPI.TokenStorageService;
import admin.AdminMain;
import auth.volleyAPI.VolleyService;
import manager.ManagerMain;
import user.UserMain;

public class Login extends AppCompatActivity {

    private TokenStorageService tokenStorage;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView regLink;
    private VolleyService volleyService;
    private static final String AUTH_API="http://192.168.0.108:8080/api/v1/auth/";

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

        regLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
                    public void onSuccess(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            String username = response.getString("username");

                            tokenStorage.saveToken(token);

                            String userUrl = "http://192.168.0.108:8080/api/v1/users/get/" + username;
                            JsonObjectRequest userRequest = new JsonObjectRequest(Request.Method.GET, userUrl, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject userResponse) {
                                            Log.d("RESPONSE", userResponse.toString());
                                            JSONArray rolesArray = null;
                                            String role;
                                            try {
                                                rolesArray = userResponse.getJSONArray("roles");
                                                JSONObject firstRoleObject = rolesArray.getJSONObject(0); // Обрати першу роль з масиву
                                                role = firstRoleObject.getString("name");
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }

                                            if (!role.isEmpty()) {
                                                if (role.equals("ROLE_ADMIN")) {
                                                    Intent intent = new Intent(Login.this, AdminMain.class);
                                                    startActivity(intent);
                                                } else if (role.equals("ROLE_MANAGER")) {
                                                    Intent intent = new Intent(Login.this, ManagerMain.class);
                                                    startActivity(intent);
                                                } else {
                                                    Intent intent = new Intent(Login.this, UserMain.class);
                                                    startActivity(intent);
                                                }
                                            } else {
                                                Toast.makeText(Login.this, "Role not received", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(Login.this, "Error getting user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Login.this, "Error processing server response", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(Login.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
