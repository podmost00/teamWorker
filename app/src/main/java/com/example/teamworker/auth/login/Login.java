package com.example.teamworker.auth.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.teamworker.R;

import com.example.teamworker.auth.volleyAPI.AuthCallback;
import com.example.teamworker.auth.volleyAPI.VolleyService;

import org.json.JSONObject;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        VolleyService volleyService = new VolleyService(this);


        String loginUrl = "http://192.168.0.108:8080/api/v1/auth/login";


        JSONObject credentials = new JSONObject();
        try {
            credentials.put("username", "your_username");
            credentials.put("password", "your_password");
        } catch (Exception e) {
            e.printStackTrace();
        }


        Button loginButton = findViewById(R.id.login_loginButton);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                volleyService.login(loginUrl, credentials, new AuthCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {

                        Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(VolleyError error) {

                        Toast.makeText(Login.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}