package com.bobbletheme.view;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.bobbletheme.R;
import com.bobbletheme.model.ThemeCategories;
import com.bobbletheme.presenter.ThemeDataAdapter;
import com.bobbletheme.presenter.ThemeLoaderInterface;
import com.bobbletheme.presenter.ThemeServerConnection;

import java.lang.reflect.Method;

public class ThemeActivity extends AppCompatActivity implements ThemeLoaderInterface {

    private RecyclerView parentRecycler;
    private GridLayoutManager gridLayoutManagerMyTheme;
    private Context context;
    private static final int REQUEST_PERMISSION = 100;
    private ThemeLoaderInterface themeLoaderInterface;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        themeLoaderInterface = this;
        setTitle("Set Keyboard Theme");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        askPermissions();
        progressBar = (ProgressBar) findViewById(R.id.themeProgress);
        parentRecycler = (RecyclerView) findViewById(R.id.parentRecycler);
        gridLayoutManagerMyTheme = new GridLayoutManager(getApplicationContext(), 1);
        parentRecycler.setLayoutManager(gridLayoutManagerMyTheme);

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
        ThemeDataAdapter dataAdapter = new ThemeDataAdapter(context, themeCategories);
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
}
