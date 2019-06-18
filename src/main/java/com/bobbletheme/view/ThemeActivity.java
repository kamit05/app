package com.bobbletheme.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bobbletheme.R;
import com.bobbletheme.model.BobbleConstants;
import com.bobbletheme.model.ThemeCategories;
import com.bobbletheme.model.Themes;
import com.bobbletheme.presenter.ThemeDataAdapter;
import com.bobbletheme.presenter.ThemeLoaderInterface;
import com.bobbletheme.presenter.ThemeServerConnection;
import com.bobbletheme.room.ThemeDatabase;
import com.bobbletheme.room.ThemeModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.util.List;

public class ThemeActivity extends AppCompatActivity implements ThemeLoaderInterface {

    private RecyclerView parentRecycler;
    private GridLayoutManager gridLayoutManagerMyTheme;
    private Context context;
    private static final int REQUEST_PERMISSION = 100;
    private ThemeLoaderInterface themeLoaderInterface;
    private ProgressBar progressBar;
    private ThemeDataAdapter dataAdapter;
    public static ThemeDatabase themeDatabase;
    public static List<ThemeModel> myThemesDataItems;
    private final int REQUEST_CODE_PICK_IMAGE = 0;
    private static ThemeActivity sAppInstance;
    ThemeCategories[] themeCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        themeLoaderInterface = this;
        sAppInstance = this;
        setTitle("Set Keyboard Theme");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        askPermissions();
        progressBar = (ProgressBar) findViewById(R.id.themeProgress);
        parentRecycler = (RecyclerView) findViewById(R.id.parentRecycler);
        gridLayoutManagerMyTheme = new GridLayoutManager(getApplicationContext(), 1);
        parentRecycler.setLayoutManager(gridLayoutManagerMyTheme);
        themeDatabase = Room.databaseBuilder(context, ThemeDatabase.class, ThemeDatabase.DB_NAME).build();
        loadAllThemes();
        ThemeServerConnection thremeServerConnection = new ThemeServerConnection(context, themeLoaderInterface);
        thremeServerConnection.makeThemeObjectRequest();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void updateUI(ThemeCategories[] themeCategories) {
        this.themeCategories = themeCategories;
        dataAdapter = new ThemeDataAdapter(context, themeCategories, progressBar);
        parentRecycler.setAdapter(dataAdapter);
        progressBar.setVisibility(View.GONE);
    }

    /***
     * Adding permission for fetching image from Gallery
     */
    protected void askPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
            return;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void loadAllThemes() {
        new AsyncTask<String, Void, List<ThemeModel>>() {
            @Override
            protected List<ThemeModel> doInBackground(String... params) {
                Log.d("ThemeDataItem", "Theme List:::::: " + themeDatabase.daoAccess().fetchAllThemes());
                return themeDatabase.daoAccess().fetchAllThemes();
            }

            @Override
            protected void onPostExecute(List<ThemeModel> themeList) {
                Log.d("ThemeDataItem", "Theme List:::::: ");
                myThemesDataItems = themeList;
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int REQUEST_CODE_CUSTOM_THEME = 1;

        if (data == null) return;

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                Intent intent = new Intent(ThemeActivity.this, CustomThemeActivity.class);
                intent.putExtra(BobbleConstants.IMAGE_URI, imageUri);
                intent.putExtra(BobbleConstants.FROM, BobbleConstants.FROM_THEME_TAB_HOME);
                startActivityForResult(intent, REQUEST_CODE_CUSTOM_THEME);
//                BobbleEvent.getInstance().log(BobbleConstants.THEME_HOME_SCREEN,
//                        "Custom theme image chosen", "custom_theme_image_chosen", "gallery",
//                        System.currentTimeMillis() / 1000, BobbleConstants.EventCategory.THREE);
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CUSTOM_THEME && data != null) {
            String customThemeJson = data.getStringExtra(BobbleConstants.CUSTOM_THEME);
            final Gson gson;
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
            Themes customTheme = gson.fromJson(customThemeJson, new TypeToken<Themes>() {
            }.getType());
            if (parentRecycler.getAdapter() != null) {
                Log.d("CustomThemeDataItem", "Custom Theme :::::: " + customTheme);
                //((ThemeDataAdapter) parentRecycler.getAdapter()).updatetheFirstAdapterList(customTheme, 1);
                //CurrentKeyboardTheme.getInstance().loadCustomTheme(context, customTheme.getThemeId());
//                KeyboardSwitcher switcher = KeyboardSwitcher.getInstance();
//                if (switcher != null) {
//                    switcher.updateOnThemeChange();
//                    showSoftKeyboard(editText);
//                }
            }
        }
    }
}
