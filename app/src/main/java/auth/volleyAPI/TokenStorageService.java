package auth.volleyAPI;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import model.Role;
import model.User;

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

    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userJson = new Gson().toJson(user);
        editor.putString(USER_KEY, userJson);
        editor.apply();
    }

    public void saveUserData(String userDataJson) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_DATA_KEY, userDataJson);
        editor.apply();
    }

    public String getUserData() {
        return sharedPreferences.getString(USER_DATA_KEY, "");
    }


    public User getUser() {
        String userJson = sharedPreferences.getString(USER_KEY, "");
        return new Gson().fromJson(userJson, User.class);
    }

    public void setRole(Role role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ROLE, role.getName());
        editor.apply();
    }

    public String getRole() {
        return sharedPreferences.getString(USER_ROLE, "");
    }

    public void logOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
