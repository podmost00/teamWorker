package services;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import callback.AuthCallback;
import callback.GetArrayCallback;
import callback.GetStringCallback;

public class VolleyService {
    private RequestQueue requestQueue;
    private Context context;
    private TokenStorageService tokenStorageService;

    public VolleyService(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
        tokenStorageService = new TokenStorageService(context);
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void login(String url, JSONObject jsonRequest, final AuthCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonRequest,
                response -> {
                    try {
                        callback.onSuccess(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    String errorMessage = error.getMessage();
                    if(errorMessage!=null) {
                        Log.e("Volley Error", errorMessage);
                    } else Log.d("Volley Error", "");
                    callback.onError(error);
                }
        );
        addToRequestQueue(jsonObjectRequest);
    }

    public void register(String url, JSONObject data, AuthCallback callback) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
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
                });


        getRequestQueue().add(request);
    }
    public void makeGetArrayRequest(String url, final GetArrayCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArrayResponse = new JSONArray(response);
                        callback.onSuccess(jsonArrayResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> callback.onError(error)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = tokenStorageService.getToken();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);
    }


    public void makeGetStringRequest(String url, final GetStringCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        callback.onSuccess(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                callback::onError) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = tokenStorageService.getToken();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void makePUTObjectRequest(String url, JSONObject jsonObject, GetArrayCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(response);
                try {
                    callback.onSuccess(jsonArray);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> callback.onError(error)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = tokenStorageService.getToken();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(request);
    }

}



