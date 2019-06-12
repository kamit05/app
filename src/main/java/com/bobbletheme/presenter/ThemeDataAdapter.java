package com.bobbletheme.presenter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bobbletheme.R;
import com.bobbletheme.model.ThemeCategories;

import java.io.File;

public class ThemeDataAdapter extends RecyclerView.Adapter<ThemeDataAdapter.ViewHolder> {
    private Context context;
    private ThemeDataItemAdapter themeDataAdapterMyTheme, themeDataAdapterAPIThemes;
    private ThemeCategories[] themeCategories;



    public ThemeDataAdapter(Context context, ThemeCategories[] themeCategories) {
        this.context = context;
        this.themeCategories = themeCategories;
    }

    @Override
    public int getItemCount() {
        return themeCategories.length + 1;
    }

    @Override
    public ThemeDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.theme_view_item, viewGroup, false);
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
        if (i == 0){
                viewHolder.txtThemeHeader.setText(context.getResources().getString(R.string.my_theme));
                viewHolder.txtViewAllThemes.setText("");
                viewHolder.txtViewAllThemes.setTag(context.getResources().getString(R.string.my_theme));
                Bitmap galleryBitmap = getLastImageFromGallery(context);
                themeDataAdapterMyTheme = new ThemeDataItemAdapter(context, null, galleryBitmap);
                viewHolder.recyclerViewMyTheme.setAdapter(themeDataAdapterMyTheme);
        } else {
                viewHolder.txtThemeHeader.setText(themeCategories[i-1].getThemeCategoryName());
                viewHolder.txtViewAllThemes.setTag(themeCategories[i-1].getThemeCategoryName());
                themeDataAdapterAPIThemes = new ThemeDataItemAdapter(context, themeCategories[i-1].themes, null);
                viewHolder.recyclerViewMyTheme.setAdapter(themeDataAdapterAPIThemes);
                if (themeCategories[i-1].themes.length < 6){
                    viewHolder.txtViewAllThemes.setText("");
                }
        }

        viewHolder.txtViewAllThemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeDataAdapterAPIThemes = new ThemeDataItemAdapter(context, themeCategories[i].themes, null);
                expandViewOnClick(view, themeDataAdapterAPIThemes);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtViewAllThemes, txtThemeHeader;
        private RecyclerView recyclerViewMyTheme;

        public ViewHolder(View view) {
            super(view);
            recyclerViewMyTheme = view.findViewById(R.id.myThemeRecycler);
            txtViewAllThemes = view.findViewById(R.id.viewAllThemes);
            txtThemeHeader = view.findViewById(R.id.mytheme);
            GridLayoutManager gridLayoutManagerMyTheme = new GridLayoutManager(context, 3);
            recyclerViewMyTheme.setLayoutManager(gridLayoutManagerMyTheme);
        }
    }
    private void expandViewOnClick(View view, ThemeDataItemAdapter themeDataAdapterAPIThemes){
        if (!themeDataAdapterAPIThemes.isExpanded()) {
            themeDataAdapterAPIThemes.setAdapterSize(themeCategories[0].themes.length);
            themeDataAdapterAPIThemes.notifyDataSetChanged();
            themeDataAdapterAPIThemes.setExpanded(true);
            ((TextView)view).setText(context.getResources().getString(R.string.theme_view_less));
        } else {
            themeDataAdapterAPIThemes.setAdapterSize(6);
            themeDataAdapterAPIThemes.setExpanded(false);
            themeDataAdapterAPIThemes.notifyDataSetChanged();
            ((TextView)view).setText(context.getResources().getString(R.string.theme_view_all));
        }
    }

    /***
     * Method Geting Image from Gallery
     * @param context
     * @return
     */
    public Bitmap getLastImageFromGallery(Context context) {
        Bitmap bm = null;
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        if (cursor.moveToFirst()) {
            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {
                bm = BitmapFactory.decodeFile(imageLocation);
            }
        }
        return bm;
    }

}