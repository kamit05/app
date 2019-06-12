package com.bobbletheme.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bobbletheme.R;
import com.bobbletheme.model.ImageUrl;
import com.bobbletheme.model.Themes;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class ThemeDataItemAdapter extends RecyclerView.Adapter<ThemeDataItemAdapter.ViewHolder> {
    private Themes[] imageUrls;
    private Context context;
    SimpleDraweeView draweeView;
    private Bitmap galleryBitmap;
    public boolean isExpanded;
    private int mAdapterSize = 0;

    public ThemeDataItemAdapter(Context context, Themes[] imageUrls, Bitmap bitmap) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.galleryBitmap = bitmap;
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
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        try {
            if (imageUrls == null) {
                draweeView.setImageBitmap(galleryBitmap);
            } else {
                    String[] themeUrltoLoad = new String[imageUrls.length];
                    themeUrltoLoad[i] = imageUrls[i].getThemePreviewImageXHDPIURL();
                    ArrayList<ImageUrl> themeUrlList = prepareData(themeUrltoLoad);
                    Uri imageUri = Uri.parse(themeUrlList.get(i).getImageUrl());
                    draweeView.setImageURI(imageUri);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    /***
     * Looping URLs to load in data adapter
     * @param imageUrls
     * @return
     */
    private ArrayList prepareData(String imageUrls[]) {
        ArrayList imageUrlListPopular = new ArrayList<>();
        for (int i = 0; i < imageUrls.length; i++) {
            ImageUrl imageUrl = new ImageUrl();
            imageUrl.setImageUrl(imageUrls[i]);
            imageUrlListPopular.add(imageUrl);
        }
        Log.d("ThemeActivity", "List count: " + imageUrlListPopular.size());
        return imageUrlListPopular;
    }

    @Override
    public int getItemCount() {
        if (imageUrls != null && !isExpanded && imageUrls.length > 6) {
            return 6;
        } else if (imageUrls != null && !isExpanded && imageUrls.length < 6) {
            return imageUrls.length;
        } else if(imageUrls != null && isExpanded) {
            return imageUrls.length;
        } else {
            return 1;
        }
    }

    public void setAdapterSize(int size) {
        this.mAdapterSize = size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
            draweeView = (SimpleDraweeView) view.findViewById(R.id.sdvImage);
        }
    }
}