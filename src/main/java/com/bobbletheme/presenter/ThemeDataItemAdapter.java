package com.bobbletheme.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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


public class ThemeDataItemAdapter extends RecyclerView.Adapter<ThemeDataItemAdapter.ViewHolder> {
    private Themes[] imageUrls;
    private Context context;
    public boolean isExpanded;
    private Bitmap lastGalleryImage;
    private final int REQUEST_CODE_PICK_IMAGE = 0;
    private ThemeDataAdapter themeDataAdapter;
    private ProgressBar progressBar;

    public ThemeDataItemAdapter(Context context, Themes[] imageUrls, ThemeDataAdapter themeDataAdapter, ProgressBar progressBar) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.themeDataAdapter = themeDataAdapter;
        this.progressBar = progressBar;
    }

    public ThemeDataItemAdapter(Context context, List<ThemeModel> myThemesDataItems, Bitmap lastGalleryImage, boolean isMyTheme, ThemeDataAdapter themeDataAdapter, ProgressBar progressBar) {
        this.context = context;
        ThemeDataAdapter.isMyTheme = isMyTheme;
        this.lastGalleryImage = lastGalleryImage;
        ThemeActivity.myThemesDataItems = myThemesDataItems;
        this.themeDataAdapter = themeDataAdapter;
        this.progressBar = progressBar;
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
              if (ThemeDataAdapter.isMyTheme) {
                  if (i == myThemesDataItems.size()){
                      viewHolder.draweeView.setImageBitmap(lastGalleryImage);
                      viewHolder.draweeView.setTag("GALLERY");
                  } else if (i == myThemesDataItems.size() + 1){
                      viewHolder.draweeView.setImageResource(R.drawable.ic_add_black_grey);
                      viewHolder.draweeView.setBackgroundResource(R.drawable.border_color);
                      viewHolder.draweeView.setTag("CUSTOM");
                  }  else {
                    if (myThemesDataItems != null && myThemesDataItems.size() != 0) {
                        if (ThemeUtils.getScreenResolution(context).contains(BobbleConstants.XXHDPI)) {
                            viewHolder.draweeView.setImageURI(myThemesDataItems.get(myThemesDataItems.size() - i -1).themePreviewImageXXHDPIURL);
                        } else if (ThemeUtils.getScreenResolution(context).contains(BobbleConstants.XHDPI)) {
                            viewHolder.draweeView.setImageURI(myThemesDataItems.get(myThemesDataItems.size() - i -1).themePreviewImageXHDPIURL);
                        } else {
                            viewHolder.draweeView.setImageURI(myThemesDataItems.get(myThemesDataItems.size() - i -1).themePreviewImageHDPIURL);
                        }
                        viewHolder.draweeView.setTag("MY THEMES");
                    }
                }
            } else {
                  if (ThemeUtils.getScreenResolution(context).contains(BobbleConstants.XXHDPI)) {
                      viewHolder.draweeView.setImageURI(imageUrls[i].getThemePreviewImageXXHDPIURL());
                  } else if (ThemeUtils.getScreenResolution(context).contains(BobbleConstants.XHDPI)) {
                      viewHolder.draweeView.setImageURI(imageUrls[i].getThemePreviewImageXHDPIURL());
                  } else {
                      viewHolder.draweeView.setImageURI(imageUrls[i].getThemePreviewImageHDPIURL());
                  }
                  viewHolder.draweeView.setTag("API THEMES");
              }

            viewHolder.draweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.draweeView.getTag() != null && viewHolder.draweeView.getTag().equals("CUSTOM")){
                        Toast.makeText(context, viewHolder.draweeView.getTag().toString(), Toast.LENGTH_SHORT).show();
                        openIntentToPickImage();
                    } else if (viewHolder.draweeView.getTag() != null  && viewHolder.draweeView.getTag().equals("GALLERY")){
                        Toast.makeText(context, viewHolder.draweeView.getTag().toString() + " " + "THEME SELECTED", Toast.LENGTH_SHORT).show();
                        return;
                    }  else if (viewHolder.draweeView.getTag() != null  && viewHolder.draweeView.getTag().equals("MY THEMES")){
                        Toast.makeText(context, viewHolder.draweeView.getTag().toString() + " " + "SELECTED", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        insertSelectedThemeInDB(imageUrls[i]);
                    }
                }
            });
            viewHolder.draweeView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (viewHolder.draweeView.getTag() != null && !viewHolder.draweeView.getTag().equals("API THEMES") && !viewHolder.draweeView.getTag().equals("CUSTOM") && !viewHolder.draweeView.getTag().equals("GALLERY")){
                        viewHolder.deleteTheme.setVisibility(View.VISIBLE);
                    }
                    return false;
                }
            });
            viewHolder.deleteTheme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteRow(myThemesDataItems.get(myThemesDataItems.size()-i-1));
                    myThemesDataItems.remove(myThemesDataItems.size()-i-1);
                    viewHolder.deleteTheme.setVisibility(View.GONE);
                    notifyItemRemoved(i);
                    ThemeDataAdapter.isMyTheme = true;
                    themeDataAdapter.notifyItemChanged(0);
                }
            });
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /***
     * Method inserting selected theme in Room Database
     * @param imageUrls
     */
    private void insertSelectedThemeInDB(Themes imageUrls) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
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

        ThemeDataAdapter.isMyTheme = true;

        if (!checkDuplicateTheme(myThemesDataItems, themeModel)) {
            myThemesDataItems.add(themeModel);
            themeDataAdapter.notifyItemChanged(0);
            insertRow(themeModel);
            Toast.makeText(context, "DOWNLOADING THEME..", Toast.LENGTH_SHORT).show();
        } else {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            Toast.makeText(context, "THEME DOWNLOADED ALREADY!", Toast.LENGTH_SHORT).show();
        }
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
                } else if (!isExpanded && myThemesDataItems.size() > 4) {
                    return 6;
                } else {
                    return myThemesDataItems.size() + 2;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView draweeView;
        private ImageView themeSelected;
        private ImageView deleteTheme;

        public ViewHolder(View view) {
            super(view);
            draweeView = view.findViewById(R.id.sdvImage);
            themeSelected = view.findViewById(R.id.themeSelected);
            deleteTheme = view.findViewById(R.id.deleteTheme);
        }
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
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }.execute(themeModel);

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

    /***
     * Method opening image picker available in device
     */
    private void openIntentToPickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select App"), REQUEST_CODE_PICK_IMAGE);
        } else {
            Toast.makeText(context, "No app found to open", Toast.LENGTH_SHORT).show();
        }
    }

    /***
     * Method checking any duplicate theme available in theme list
     * @param themeModels
     * @param themeModel
     * @return
     */
    private boolean checkDuplicateTheme(List<ThemeModel> themeModels, ThemeModel themeModel){
        for (int x = 0; x <themeModels.size(); x++){
            if(themeModels.get(x).themeName.equalsIgnoreCase(themeModel.themeName)) {
                return true;
            }
        }
        return false;
    }
}