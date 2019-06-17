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
import android.widget.ImageView;
import android.widget.TextView;

import com.bobbletheme.R;
import com.bobbletheme.model.ThemeCategories;

import java.io.File;

import static com.bobbletheme.view.ThemeActivity.myThemesDataItems;

public class ThemeDataAdapter extends RecyclerView.Adapter<ThemeDataAdapter.ViewHolder> {
    private Context context;
    private ThemeDataItemAdapter themeDataAdapterMyTheme;
    private ThemeDataItemAdapter themeDataAdapterAPIThemes;
    private ThemeCategories[] themeCategories;
    private boolean isMyTheme =false;

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
                viewHolder.txtViewAllThemes.setTag(context.getResources().getString(R.string.my_theme));
                viewHolder.txtViewAllThemes.setVisibility(View.INVISIBLE);
                isMyTheme = true;
                themeDataAdapterMyTheme = new ThemeDataItemAdapter(context,myThemesDataItems, getLastImageFromGallery(context), isMyTheme);
                viewHolder.recyclerViewMyTheme.setAdapter(themeDataAdapterMyTheme);

        } else {
                isMyTheme = false;
                viewHolder.txtThemeHeader.setText(themeCategories[i-1].getThemeCategoryName());
                viewHolder.txtViewAllThemes.setTag(themeCategories[i-1].getThemeCategoryName());
                themeDataAdapterAPIThemes = new ThemeDataItemAdapter(context, themeCategories[i-1].themes);
                viewHolder.recyclerViewMyTheme.setAdapter(themeDataAdapterAPIThemes);
                if (themeCategories[i-1].themes.length < 6){
                    viewHolder.txtViewAllThemes.setVisibility(View.INVISIBLE);
                }
        }

        viewHolder.txtViewAllThemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThemeDataItemAdapter adapter = (ThemeDataItemAdapter) viewHolder.recyclerViewMyTheme.getAdapter();
                if (view.getTag().equals(context.getResources().getString(R.string.my_theme))){
                    expandViewOnClick(view, adapter, myThemesDataItems.size());
                } else {
                    expandViewOnClick(view, adapter, themeCategories[i - 1].themes.length);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtThemeHeader;
        private RecyclerView recyclerViewMyTheme;
        private ImageView txtViewAllThemes;

        public ViewHolder(View view) {
            super(view);
            recyclerViewMyTheme = view.findViewById(R.id.myThemeRecycler);
            txtViewAllThemes = view.findViewById(R.id.viewAllThemes);
            txtThemeHeader = view.findViewById(R.id.mytheme);
            GridLayoutManager gridLayoutManagerMyTheme = new GridLayoutManager(context, 3);
            recyclerViewMyTheme.setLayoutManager(gridLayoutManagerMyTheme);
        }
    }
    private void expandViewOnClick(View view, ThemeDataItemAdapter themeDataAdapterAPIThemes, int length){
        try {
            if (!themeDataAdapterAPIThemes.isExpanded()) {
                themeDataAdapterAPIThemes.notifyItemRangeInserted(6,  length-1);
                themeDataAdapterAPIThemes.setExpanded(true);
                ((ImageView) view).setImageResource(R.drawable.ic_keyboard_arrow_up_grey);
            } else {
                themeDataAdapterAPIThemes.setExpanded(false);
                themeDataAdapterAPIThemes.notifyItemRangeRemoved(6, length-1);
                ((ImageView) view).setImageResource(R.drawable.ic_keyboard_arrow_down_grey);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /***
     * Method Geting Image from Gallery
     * @param context
     * @return
     */
    public Bitmap getLastImageFromGallery(Context context) {
        Bitmap bm = null;
        String imageLocation = "";
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
            imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {
                bm = BitmapFactory.decodeFile(imageLocation);
            }
        }
        return bm;
    }
}