package com.bobbletheme.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bobbletheme.R;
import com.bobbletheme.imagecropper.CropView;
import com.bobbletheme.model.BobbleConstants;
import com.bobbletheme.model.Themes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class CustomThemeScreenTwo extends AbstractFragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private CropView cropView;
    private SeekBar alphaValueSeekBar;
    private Bitmap imageBitmap;
    //private BobblePrefs bobblePrefs;
    private Gson GSON = new GsonBuilder().serializeNulls().create();
    private TextView alphaValueTv;
    private Button doneBtn;
    private boolean isBrightnessSeekBarTouchedOnce = false;
    private String screeName;

    public CustomThemeScreenTwo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_custom_theme_screen_two, container, false);
        alphaValueSeekBar = (SeekBar) fragmentView.findViewById(R.id.alpha_seek_bar);
        alphaValueSeekBar.setOnSeekBarChangeListener(this);
        alphaValueTv = (TextView) fragmentView.findViewById(R.id.alpha_value_tv);
        alphaValueTv.setText("40%");
        cropView = (CropView) fragmentView.findViewById(R.id.image_cropper);
        cropView.setViewportRatio(1.25f);
        cropView.setViewportOverlayPadding(40);
        cropView.setBorderNeeded(false);
        cropView.setViewportOverlayColor(Color.parseColor("#4a4949"));
        cropView.disableTransformation();
        cropView.drawDummyKeyBoard(true);
        cropView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        if (imageBitmap != null && cropView.getImageBitmap() == null) {
            cropView.setImageBitmap(imageBitmap);
        }
        doneBtn = (Button) fragmentView.findViewById(R.id.custom_fragment_2_done_btn);
        doneBtn.setOnClickListener(this);
        if (CustomThemeActivity.from == BobbleConstants.FROM_KEYBOARD) {
            screeName = BobbleConstants.KEYBOARD_VIEW;
        } else if (CustomThemeActivity.from == BobbleConstants.FROM_APP) {
            screeName = BobbleConstants.KEYBOARD_SETTINGS_SCREEN;
        } else if (CustomThemeActivity.from == BobbleConstants.FROM_THEME_TAB_HOME) {
            screeName = BobbleConstants.THEME_HOME_SCREEN;
        }
        return fragmentView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
       // bobblePrefs = BobbleApp.getInstance().getBobblePrefs();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        imageBitmap = bitmap;
        if (cropView != null && cropView.getImageBitmap() == null) {
            cropView.setImageBitmap(imageBitmap);
        }
    }

    public void onImageTransfrom(Bundle bundle) {
        float scale = bundle.getFloat("scale");
        float x = bundle.getFloat("positionX");
        float y = bundle.getFloat("positionY");
        cropView.transFormImageForceFully(scale, x, y);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float alphaValue = ((float) (100 - progress) / 100);
        cropView.changeDummyKeyboardAlphaValue(alphaValue);
        alphaValueTv.setText(progress + "%");
        if (!isBrightnessSeekBarTouchedOnce) {
            isBrightnessSeekBarTouchedOnce = true;
//            BobbleEvent.getInstance().log(screeName,
//                    "Custom theme adjusted brightness", "custom_theme_adjusted_brightness", "",
//                    System.currentTimeMillis() / 1000, BobbleConstants.EventCategory.THREE);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.custom_fragment_2_done_btn) {
            doneBtn.setOnClickListener(null);
            Context context = getContext().getApplicationContext();
//            String customThemeBackgroundPath = SaveUtils.saveCustomThemeBackgroundToSDCard(context, cropView.crop());
//            Bitmap checkerBitmap = BitmapFactory.decodeFile(customThemeBackgroundPath);
//            if (FileUtil.isFilePresent(context, customThemeBackgroundPath) && checkerBitmap != null) {
//                checkerBitmap.recycle();
//                float alphaValue = ((float) (100 - alphaValueSeekBar.getProgress()) / 100);
//                final Themes customTheme = GSON.fromJson(bobblePrefs.defaultCustomThemeParameters().get(), Themes.class);
//                customTheme.setStoredThemeBackgroundImage(customThemeBackgroundPath);
//                customTheme.setThemeId(bobblePrefs.lastCustomThemeId().get() - 1);
//                bobblePrefs.lastCustomThemeId().put(customTheme.getThemeId());
//                customTheme.setThemePreviewImage(customThemeBackgroundPath);
//                customTheme.setKeyboardBackgroundOpacity(alphaValue);
//                customTheme.setThemeType(BobbleConstants.IMAGE_THEME);
//                customTheme.setThemeName(BobbleConstants.CUSTOM_THEME);
//                Utils.addCustomTheme(customTheme, bobblePrefs);
//                bobblePrefs.currentThemeId().put(customTheme.getThemeId());
//                CurrentKeyboardTheme.getInstance().loadCustomTheme(context, customTheme.getThemeId());
//                KeyboardSwitcher keyboardSwitcher = KeyboardSwitcher.getInstance();
//                if (keyboardSwitcher != null) {
//                    keyboardSwitcher.updateOnThemeChange();
//                }
//                if (CustomThemeActivity.from == BobbleConstants.FROM_KEYBOARD) {
//                    getActivity().finish();
//                } else {
//                    BobbleCore.getInstance().getExecutorSupplier().forMainThreadTasks().execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            final String customThemeString = BobbleApp.getInstance().getGson().toJson(customTheme);
//                            mListener.onDonePressed(customThemeString);
//                        }
//                    });
//
//                }
//                BobbleEvent.getInstance().log(screeName,
//                        "Custom theme crop done", "custom_theme_crop_done", "",
//                        System.currentTimeMillis() / 1000, BobbleConstants.EventCategory.THREE);
//                getActivity().finish();
//
//                BobbleEvent.getInstance().log(screeName,
//                        "Custom theme adjusted brightness done", "custom_theme_adjusted_brightness_done", alphaValueSeekBar.getProgress() + "",
//                        System.currentTimeMillis() / 1000, BobbleConstants.EventCategory.THREE);
//            } else {
//                if (new File(customThemeBackgroundPath).exists()) {
//                    FileUtil.delete(customThemeBackgroundPath);
//                }
//                if (getActivity() != null) {
//                    getActivity().finish();
//                }
//                Toast.makeText(context, R.string.failed_to_create, Toast.LENGTH_SHORT).show();
//            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onDonePressed(String customThemeJson);
    }
}
