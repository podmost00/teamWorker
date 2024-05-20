package auth.volleyAPI;

import com.android.volley.VolleyError;

import org.json.JSONException;

public interface GetStringCallback {
    void onSuccess(String response) throws JSONException;
    void onError(VolleyError error);
}
