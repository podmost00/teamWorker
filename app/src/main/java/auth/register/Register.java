package auth.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.teamworker.R;
import auth.login.Login;
import auth.volleyAPI.AuthCallback;
import auth.volleyAPI.VolleyService;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private TextView loginLink;
    private static final String AUTH_API = "http://192.168.0.108:8080/api/v1/auth/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginLink = findViewById(R.id.register_label2);
        usernameEditText = findViewById(R.id.register_firstName);
        surnameEditText = findViewById(R.id.register_secondName);
        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_regButton);

        loginLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String surname = surnameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();


                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }


                JSONObject userData = new JSONObject();
                try {
                    userData.put("username", email);
                    userData.put("password", password);
                    userData.put("name", username);
                    userData.put("surname", surname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                VolleyService volleyService = new VolleyService(Register.this);
                volleyService.register(AUTH_API + "register", userData, new AuthCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(Register.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
