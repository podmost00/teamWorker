package auth.volleyAPI;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;

public interface GetArrayCallback {
    void onSuccess(JSONArray response) throws JSONException;
    void onError(VolleyError error);
}
