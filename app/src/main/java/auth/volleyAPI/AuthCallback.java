package auth.volleyAPI;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public interface AuthCallback {
    void onSuccess(JSONObject response) throws JSONException;
    void onError(VolleyError error);
}
