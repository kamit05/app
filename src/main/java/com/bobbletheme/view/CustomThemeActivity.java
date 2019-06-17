package com.bobbletheme.view;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.bobbletheme.R;
import com.bobbletheme.model.BobbleConstants;
import com.bobbletheme.presenter.ThemeUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.InvocationTargetException;


public class CustomThemeActivity extends AppCompatActivity implements CustomThemeScreenOne.OnFragmentInteractionListener, CustomThemeScreenTwo.OnFragmentInteractionListener {

    CustomViewPager customThemeScreenVp;
    FragmentManager fragmentManager;
    Bitmap imageBitmap;
    public static int from = -1;
    private boolean isCustomThemeImageModifiedOnce = false;
    private String screenName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_theme);

        if (getIntent() != null) {
            getImageBitmap();
            from = getIntent().getIntExtra(BobbleConstants.FROM, -1);
            if (from == BobbleConstants.FROM_KEYBOARD) {
                screenName = BobbleConstants.KEYBOARD_VIEW;
            } else if (from == BobbleConstants.FROM_APP){
                screenName = BobbleConstants.KEYBOARD_SETTINGS_SCREEN;
            } else if (from == BobbleConstants.FROM_THEME_TAB_HOME) {
                screenName = BobbleConstants.THEME_HOME_SCREEN;
            }
        }

        if (imageBitmap != null) {
            customThemeScreenVp = (CustomViewPager) findViewById(R.id.custom_theme_screen_viewpager);
            customThemeScreenVp.setPagingEnabled(false);
            fragmentManager = getSupportFragmentManager();
            customThemeScreenVp.setAdapter(new CustomScreenViewPagerAdapter(fragmentManager));
        } else {
            Toast.makeText(getApplicationContext(), R.string.some_error_occured, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onNextPressed() {
        customThemeScreenVp.setCurrentItem(1);
    }

    @Override
    public void onImageTransForm(Bundle bundle) {
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null && fragment instanceof CustomThemeScreenTwo) {
                ((CustomThemeScreenTwo)fragment).onImageTransfrom(bundle);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void onEventMainThread(String actionType) {
        if (actionType.equals(BobbleConstants.ACTION_RESIZE_OR_MOVE)) {
            if (!isCustomThemeImageModifiedOnce) {
                isCustomThemeImageModifiedOnce = true;
//                BobbleEvent.getInstance().log(screenName,
//                        "Custom theme crop resize", "custom_theme_crop_resize", "",
//                        System.currentTimeMillis() / 1000, BobbleConstants.EventCategory.THREE);
            }
        }
    }

    @Override
    public void onDonePressed(String customThemeJson) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(BobbleConstants.CUSTOM_THEME, customThemeJson);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void getImageBitmap() {
        Uri imageUri = (Uri) (getIntent().hasExtra(BobbleConstants.IMAGE_URI) ? getIntent().getParcelableExtra(BobbleConstants.IMAGE_URI) : "");
        int keyBoardWidth = getRealScreenSize(getApplicationContext()).x;
        int keyBoardHeight = getSharedPreferences(getApplicationContext()).getInt(BobbleConstants.PREF_KEYBOARD_HEIGHT, ThemeUtils.getDefaultKeyboardHeight(getResources()));
        imageBitmap = ThemeUtils.decodeSampledBitmapFromStrem(imageUri, keyBoardWidth, keyBoardHeight, getApplicationContext());
    }


    private class CustomScreenViewPagerAdapter extends FragmentPagerAdapter {


        CustomScreenViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            AbstractFragment themeScreenFragment;
            if (position == 0) {
                themeScreenFragment = new CustomThemeScreenOne();
            } else {
                themeScreenFragment = new CustomThemeScreenTwo();
            }
            themeScreenFragment.setBitmap(imageBitmap);
            return themeScreenFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (customThemeScreenVp != null && customThemeScreenVp.getCurrentItem() == 1) {
                customThemeScreenVp.setCurrentItem(0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }

        return size;
    }
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("BobblePrefs", 0);
    }
}