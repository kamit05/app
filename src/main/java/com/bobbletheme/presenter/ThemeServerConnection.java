package com.bobbletheme.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bobbletheme.model.ThemeCategories;
import com.bobbletheme.model.ThemeVariation;
import com.bobbletheme.view.ThemeActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;


public class ThemeServerConnection {

    private static final String TAG = ThemeActivity.class.getSimpleName();
    private static final String THEME_URL = "https://api.bobbleapp.me/v4/bobbleKeyboardThemes/getList";
    private Context context;
    private SharedPreferences.Editor prefsEditor;


    private ThemeLoaderInterface themeLoaderInterface;


    public ThemeServerConnection(Context context, ThemeLoaderInterface themeLoaderInterface, SharedPreferences.Editor prefsEditor) {
        this.context = context;
        this.themeLoaderInterface = themeLoaderInterface;
        this.prefsEditor = prefsEditor;
    }
    /***
     * Method making API call and loading the response in POJO
     */
    public void makeThemeObjectRequest() {
        AndroidNetworking.get(THEME_URL)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .setAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                        Log.d(TAG, " timeTakenInMillis : " + timeTakenInMillis);
                        Log.d(TAG, " bytesSent : " + bytesSent);
                        Log.d(TAG, " bytesReceived : " + bytesReceived);
                        Log.d(TAG, " isFromCache : " + isFromCache);
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final Gson gson;
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gson = gsonBuilder.create();
                        try {
                            ThemeVariation responseObject = gson.fromJson(response.toString(), ThemeVariation.class);
                            ThemeCategories[] themeCategories = responseObject.getThemeCategories();
                            String json = gson.toJson(responseObject);
                            prefsEditor.putString("OFFLINE_THEMES", json);
                            prefsEditor.commit();
                            themeLoaderInterface.updateUI(themeCategories);
                        } catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() != 0) {
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }
}
