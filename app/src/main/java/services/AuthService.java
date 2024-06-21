package services;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import callback.AuthCallback;

public class AuthService {
    private RequestQueue requestQueue;
    private static AuthService instance;

    private AuthService(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public static synchronized AuthService getInstance(RequestQueue requestQueue) {
        if (instance == null) {
            instance = new AuthService(requestQueue);
        }
        return instance;
    }

    public void login(String url, JSONObject jsonRequest, final AuthCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
