package auth.volleyAPI;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class VolleyService {
    private RequestQueue requestQueue;
    private Context context;

    public VolleyService(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
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
                callback::onSuccess,
                error -> {
                    String errorMessage = error.getMessage();
                    if(errorMessage!=null) {
                        Log.e("Volley Error", errorMessage);
                    }else Log.d("Volley Error", "NULL");
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

                        callback.onSuccess(response);
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

}
