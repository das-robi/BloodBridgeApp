package com.robindas.bloodbridge.API;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREFS_NAME = "BloodBridgePref";
    private static final String KEY_TOKEN = "jwt_token";

    private static SharedPreferences sharedPreferences;

    public TokenManager(Context context) {

        sharedPreferences = context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );
    }


    //Save token
    public void saveToken(String token){
        sharedPreferences.edit()
                .putString(KEY_TOKEN, token)
                .apply();
    }

    //Get Token
    public String getToken(){
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    //Clear token
    public void clearToken(){
        sharedPreferences.edit()
                .remove(KEY_TOKEN)
                .apply();
    }
}
