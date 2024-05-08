package com.example.teamworker.auth.volleyAPI;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface AuthCallback {
    void onSuccess(JSONObject response);
    void onError(VolleyError error);
}
