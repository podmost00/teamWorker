package services;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenStorageService {
    private static final String TOKEN_KEY = "auth-token";
    private static final String USER_KEY = "auth-user";
    private static final String USER_ROLE = "auth-user-role";
    private static final String USER_DATA_KEY = "auth-user-data";

    private SharedPreferences sharedPreferences;
    private Context context;
    public TokenStorageService(Context context) {
        this.sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, "");
    }


    public void logOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
