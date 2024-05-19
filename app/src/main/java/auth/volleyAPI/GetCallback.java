package auth.volleyAPI;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface GetCallback {
    void onSuccess(JSONArray response) throws JSONException;
    void onError(VolleyError error);
}
