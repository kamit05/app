package com.bobbletheme.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bobbletheme.R;
import com.bobbletheme.model.BobbleConstants;
import com.bobbletheme.model.Themes;
import com.bobbletheme.room.ThemeModel;
import com.bobbletheme.view.ThemeActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import static com.bobbletheme.view.ThemeActivity.myThemesDataItems;
import static com.bobbletheme.view.ThemeActivity.themeDatabase;


public class ThemeDataItemAdapter extends RecyclerView.Adapter<ThemeDataItemAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener{
    private Themes[] imageUrls;
    private Context context;
    public boolean isExpanded;
    private int mAdapterSize = 0;
    public static boolean imageAdded = true;
    private boolean isMyTheme;
    private Bitmap lastGalleryImage;
    private final int REQUEST_CODE_PICK_IMAGE = 0;
    private ThemeModel themeModelSelected;

    public ThemeDataItemAdapter(Context context, Themes[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    public ThemeDataItemAdapter(Context context, List<ThemeModel> myThemesDataItems, Bitmap lastGalleryImage, boolean isMyTheme) {
        this.context = context;
        this.isMyTheme = isMyTheme;
        this.lastGalleryImage = lastGalleryImage;
        ThemeActivity.myThemesDataItems = myThemesDataItems;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;

    }
    public boolean isExpanded() {
        return isExpanded;
    }


    @Override
    public ThemeDataItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Fresco.initialize(context);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * gets the image url from adapter and passes to Glide API to load the image
     *
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        try {
              if (isMyTheme) {
                  if (i == 0){
                      viewHolder.draweeView.setImageResource(R.drawable.ic_add_black_grey);
                      viewHolder.draweeView.setTag("CUSTOM");
                  }  else if (i == 1){
                      viewHolder.draweeView.setImageBitmap(lastGalleryImage);
                      viewHolder.draweeView.setTag("GALLERY");
                      viewHolder.themeSelected.setVisibility(View.VISIBLE);
                  } else {
                    if (myThemesDataItems != null && myThemesDataItems.size() != 0) {
                        if (getScreenResolution().contains(BobbleConstants.XXHDPI)) {
                            viewHolder.draweeView.setImageURI(myThemesDataItems.get(i-2).themePreviewImageXXHDPIURL);
                        } else if (getScreenResolution().contains(BobbleConstants.XHDPI)) {
                            viewHolder.draweeView.setImageURI(myThemesDataItems.get(i-2).themePreviewImageXHDPIURL);
                        } else {
                            viewHolder.draweeView.setImageURI(myThemesDataItems.get(i-2).themePreviewImageHDPIURL);
                        }
                        viewHolder.draweeView.setTag("MY THEMES");
                    }
                }
            } else {
                  if (getScreenResolution().contains(BobbleConstants.XXHDPI)) {
                      viewHolder.draweeView.setImageURI(imageUrls[i].getThemePreviewImageXXHDPIURL());
                  } else if (getScreenResolution().contains(BobbleConstants.XHDPI)) {
                      viewHolder.draweeView.setImageURI(imageUrls[i].getThemePreviewImageXHDPIURL());
                  } else {
                      viewHolder.draweeView.setImageURI(imageUrls[i].getThemePreviewImageHDPIURL());
                  }
              }

            viewHolder.draweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.draweeView.getTag() != null && viewHolder.draweeView.getTag().equals("CUSTOM")){
                        Toast.makeText(context, viewHolder.draweeView.getTag().toString(), Toast.LENGTH_SHORT).show();
                        openIntentToPickImage();
                    } else if (viewHolder.draweeView.getTag() != null  && viewHolder.draweeView.getTag().equals("GALLERY")){
                        Toast.makeText(context, viewHolder.draweeView.getTag().toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }  else if (viewHolder.draweeView.getTag() != null  && viewHolder.draweeView.getTag().equals("MY THEMES")){
                        Toast.makeText(context, viewHolder.draweeView.getTag().toString(), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        fetchTodoById(Integer.parseInt(imageUrls[i].getThemeId()), imageUrls[i]);
                    }
                }
            });
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void insertSelectedThemeInDB(Themes imageUrls) {
        ThemeModel themeModel = new ThemeModel();
        themeModel.themeName = imageUrls.getThemeName();
        themeModel.themeType = imageUrls.getThemeType();
        themeModel.isLightTheme = imageUrls.getIsLightTheme();

        themeModel.gestureFloatingPreviewColor = imageUrls.getGestureFloatingPreviewColor();
        themeModel.gestureFloatingPreviewTextColor = imageUrls.getGestureFloatingPreviewTextColor();

        themeModel.themePreviewImageHDPIURL = imageUrls.getThemePreviewImageHDPIURL();
        themeModel.themePreviewImageXHDPIURL = imageUrls.getThemePreviewImageXHDPIURL();
        themeModel.themePreviewImageXXHDPIURL = imageUrls.getThemePreviewImageXXHDPIURL();
        themeModel.themePreviewImageORIGINALURL = imageUrls.getThemePreviewImageORIGINALURL();

        themeModel.themeBackgroundImageHDPIURL = imageUrls.getThemeBackgroundImageHDPIURL();
        themeModel.themeBackgroundImageXHDPIURL = imageUrls.getThemeBackgroundImageXHDPIURL();
        themeModel.themeBackgroundImageXXHDPIURL = imageUrls.getThemeBackgroundImageXXHDPIURL();
        themeModel.themeBackgroundImageORIGINALURL = imageUrls.getThemeBackgroundImageORIGINALURL();

        themeModel.enterKeyCircleBackgroundColor = imageUrls.getEnterKeyCircleBackgroundColor();
        themeModel.feedbackBarPopupBackgroundColor = imageUrls.getFeedbackBarPopupBackgroundColor();
        themeModel.keyBackgroundColor = imageUrls.getKeyBackgroundColor();
        themeModel.keyboardBackgroundColor = imageUrls.getKeyboardBackgroundColor();
        themeModel.emojiRowBackgroundColor = imageUrls.getEmojiRowBackgroundColor();

        themeModel.keyboardBackgroundOpacity = imageUrls.getKeyboardBackgroundOpacity();
        themeModel.keyPopUpPreviewBackgroundColor = imageUrls.getKeyPopUpPreviewBackgroundColor();
        themeModel.moreSuggestionsButtonBackgroundColor = imageUrls.getMoreSuggestionsButtonBackgroundColor();
        themeModel.moreSuggestionsPanelBackgroundColor = imageUrls.getMoreSuggestionsPanelBackgroundColor();
        themeModel.suggestionsBarBackgroundColor = imageUrls.getSuggestionsBarBackgroundColor();
        themeModel.moreSuggestionsButtonBackgroundColor = imageUrls.getMoreSuggestionsButtonBackgroundColor();

        themeModel.keyBorderRadius = imageUrls.getKeyBorderRadius();
        themeModel.keyTextColor = imageUrls.getKeyTextColor();
        themeModel.keyVerticalGap = imageUrls.getKeyVerticalGap();
        themeModel.enterKeyBorderRadius = imageUrls.getEnterKeyBorderRadius();
        themeModel.showNonAlphaKeyBorder = imageUrls.getShowNonAlphaKeyBorder();

        themeModel.hintLabelColor = imageUrls.getHintLabelColor();
        themeModel.hintLetterColor = imageUrls.getHintLetterColor();

        themeModel.suggestionsBarIconSelectedColor = imageUrls.getSuggestionsBarIconSelectedColor();
        themeModel.suggestionsBarPageIndicatorColor = imageUrls.getSuggestionsBarPageIndicatorColor();
        themeModel.suggestionsColorAutoCorrect = imageUrls.getSuggestionsColorAutoCorrect();
        themeModel.suggestionsColorSuggested = imageUrls.getSuggestionsColorSuggested();
        themeModel.suggestionsColorTypedWord = imageUrls.getSuggestionsColorTypedWord();
        themeModel.suggestionsColorValidTypedWord = imageUrls.getSuggestionsColorValidTypedWord();

        themeModel.feedbackBarPopupBackgroundColor = imageUrls.getFeedbackBarPopupBackgroundColor();
        themeModel.swipeGestureTrailColor = imageUrls.getSwipeGestureTrailColor();
        themeModel.functionalTextColor = imageUrls.getFunctionalTextColor();
        themeModel.bobbleBar = imageUrls.getBobbleBar();

        insertRow(themeModel);
        loadAllThemes();
    }

    @Override
    public int getItemCount() {
        try {
            if (imageUrls != null && !isExpanded && imageUrls.length > 6) {
                return 6;
            } else if (imageUrls != null && !isExpanded && imageUrls.length < 6) {
                return imageUrls.length;
            } else if (imageUrls != null && isExpanded) {
                return imageUrls.length;
            } else {
                if (myThemesDataItems == null || myThemesDataItems.size() == 0) {
                    return 2;
                } else if (!isExpanded && myThemesDataItems.size() > 0){
                    return myThemesDataItems.size() + 2;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public void setAdapterSize(int size) {
        this.mAdapterSize = size;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView draweeView;
        private ImageView themeSelected;

        public ViewHolder(View view) {
            super(view);
            draweeView = view.findViewById(R.id.sdvImage);
            themeSelected = view.findViewById(R.id.themeSelected);
        }
    }

    private String getScreenResolution(){
        float screenDensity = context. getResources().getDisplayMetrics().density;
        if (screenDensity >= 4.0) {
            return "xxxhdpi";
        }
        if (screenDensity >= 3.0) {
            return "xxhdpi";
        }
        if (screenDensity >= 2.0) {
            return "xhdpi";
        }
        if (screenDensity >= 1.5) {
            return "hdpi";
        }
        if (screenDensity >= 1.0) {
            return "mdpi";
        }
        return "ldpi";
    }


    @SuppressLint("StaticFieldLeak")
    private void insertRow(ThemeModel themeModel) {
        new AsyncTask<ThemeModel, Void, Long>() {
            @Override
            protected Long doInBackground(ThemeModel... params) {
                return themeDatabase.daoAccess().insertTheme(params[0]);
            }

            @Override
            protected void onPostExecute(Long id) {
                super.onPostExecute(id);
            }
        }.execute(themeModel);

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
                isMyTheme = true;
                myThemesDataItems.clear();
                myThemesDataItems.addAll(themeList);
                ThemeActivity.dataAdapter.notifyItemChanged(0);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchTodoById(int themeID, final Themes themesToBeInserted) {
        new AsyncTask<Integer, Void, ThemeModel>() {
            @Override
            protected ThemeModel doInBackground(Integer... params) {
                return themeDatabase.daoAccess().fetchThemeListById(params[0]);
            }
            @Override
            protected void onPostExecute(ThemeModel themeModel) {
                super.onPostExecute(themeModel);
                if (themeModel == null) {
                    insertSelectedThemeInDB(themesToBeInserted);
                } else {
                    Toast.makeText(context, "Theme downloaded already!", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(themeID);
    }

    @SuppressLint("StaticFieldLeak")
    private void deleteRow(ThemeModel themeModel) {
        new AsyncTask<ThemeModel, Void, Integer>() {
            @Override
            protected Integer doInBackground(ThemeModel... params) {
                return themeDatabase.daoAccess().deleteTheme(params[0]);
            }

            @Override
            protected void onPostExecute(Integer number) {
                super.onPostExecute(number);

            }
        }.execute(themeModel);
    }


    @SuppressLint("StaticFieldLeak")
    private void updateRow(ThemeModel themeModel) {
        new AsyncTask<ThemeModel, Void, Integer>() {
            @Override
            protected Integer doInBackground(ThemeModel... params) {
                return themeDatabase.daoAccess().updateTheme(params[0]);
            }

            @Override
            protected void onPostExecute(Integer number) {
                super.onPostExecute(number);

            }
        }.execute(themeModel);

    }
    private void openIntentToPickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select App"), REQUEST_CODE_PICK_IMAGE);
        } else {
            Toast.makeText(context, "No app found to open", Toast.LENGTH_SHORT).show();
        }
    }
}