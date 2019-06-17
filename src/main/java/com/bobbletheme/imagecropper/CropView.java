/*
 * Copyright (C) 2015 Lyft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bobbletheme.imagecropper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bobbletheme.R;

import java.io.File;
import java.io.OutputStream;


/**
 * An {@link ImageView} with a fixed viewport and cropping capabilities.
 */
public class CropView extends ImageView {

    private static final int MAX_TOUCH_POINTS = 2;
    private TouchManager touchManager;
    private CropViewConfig config;

    private Paint viewportPaint = new Paint();
    private Paint dummyKeyBoardBackgroundPaint = new Paint();
    private Paint borderPaint = new Paint();
    private Paint bitmapPaint = new Paint();

    private Bitmap bitmap;
    private Bitmap dummyKeyBoard;
    private Matrix transform = new Matrix();
    private Extensions extensions;
    private OnImageTransformListener onImageTransformListener;
    private boolean isTransformationEnabled = true;
    private boolean isDummyKeyBoardNeeded = false;
    private final int DEFAULT_ALPHA_VALUE = 153;
    private boolean isBorderNeeded = true;

    public CropView(Context context) {
        super(context);
        initCropView(context, null);
    }

    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initCropView(context, attrs);
    }

    void initCropView(Context context, AttributeSet attrs) {
        config = CropViewConfig.from(context, attrs);
        touchManager = new TouchManager(MAX_TOUCH_POINTS, config);
        bitmapPaint.setFilterBitmap(true);
        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);
        borderPaint.setColor(ResourcesCompat.getColor(context.getResources(), R.color.white, null));
        dummyKeyBoardBackgroundPaint = new Paint();
        dummyKeyBoardBackgroundPaint.setStyle(Paint.Style.FILL);
        dummyKeyBoardBackgroundPaint.setColor(ResourcesCompat.getColor(context.getResources(), R.color.black, null));
        dummyKeyBoardBackgroundPaint.setAlpha(DEFAULT_ALPHA_VALUE);
        setViewportOverlayColor(config.getViewportOverlayColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bitmap == null) {
            return;
        }

        drawBitmap(canvas);
        drawOverlay(canvas);

        if (isDummyKeyBoardNeeded) {
            drawDummyKeyBoardBitmapAndBackground(canvas);
        }

        if (onImageTransformListener != null) {
            Bundle bundle = new Bundle();
            bundle.putFloat("positionX", touchManager.getTouchPoint().getX());
            bundle.putFloat("positionY", touchManager.getTouchPoint().getY());
            bundle.putFloat("scale", touchManager.getScale());
            onImageTransformListener.onImageTransform(bundle);
        }
    }

    private void drawDummyKeyBoardBitmapAndBackground(Canvas canvas) {
        final int viewportWidth = touchManager.getViewportWidth();
        final int viewportHeight = touchManager.getViewportHeight();
        final int left = (getWidth() - viewportWidth) / 2;
        final int top = (getHeight() - viewportHeight) / 2;
        final int bottom = getHeight() - (getHeight() - viewportHeight) / 2;
        final int right = getWidth() - (getWidth() - viewportWidth) / 2;

        if (dummyKeyBoard == null) {
            dummyKeyBoard = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_keyboard_faded), viewportWidth, viewportHeight, true);
        }
        canvas.drawRect(left, top, right, bottom, dummyKeyBoardBackgroundPaint);
        canvas.drawBitmap(dummyKeyBoard, left, top, bitmapPaint);
    }

    private void drawBitmap(Canvas canvas) {
        transform.reset();
        if (!touchManager.isInsideViewPort()) {
            touchManager.ensureInsideViewport();
        }
        touchManager.applyPositioningAndScale(transform);
        canvas.drawBitmap(bitmap, transform, bitmapPaint);
    }

    public void changeDummyKeyboardAlphaValue(float alphaValue) {
        int alphaValueActual = (int) (alphaValue * 255);
        dummyKeyBoardBackgroundPaint.setAlpha(alphaValueActual);
        invalidate();
    }

    public void setBorderNeeded(boolean borderNeeded) {
        isBorderNeeded  = borderNeeded;
    }

    private void drawOverlay(Canvas canvas) {
        final int viewportWidth = touchManager.getViewportWidth();
        final int viewportHeight = touchManager.getViewportHeight();
        final int left = (getWidth() - viewportWidth) / 2;
        final int top = (getHeight() - viewportHeight) / 2;

        canvas.drawRect(0, top, left, getHeight() - top, viewportPaint);
        canvas.drawRect(0, 0, getWidth(), top, viewportPaint);
        canvas.drawRect(getWidth() - left, top, getWidth(), getHeight() - top, viewportPaint);
        canvas.drawRect(0, getHeight() - top, getWidth(), getHeight(), viewportPaint);

        if (isBorderNeeded) {
            canvas.drawRect(left, top, getWidth() - left, getHeight() - top, borderPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resetTouchManager();
    }

    /**
     * Sets the color of the viewport overlay
     *
     * @param viewportOverlayColor The color to use for the viewport overlay
     */
    public void setViewportOverlayColor(@ColorInt int viewportOverlayColor) {
        viewportPaint.setColor(viewportOverlayColor);
        config.setViewportOverlayColor(viewportOverlayColor);
    }

    /**
     * Sets the padding for the viewport overlay
     *
     * @param viewportOverlayPadding The new padding of the viewport overlay
     */
    public void setViewportOverlayPadding(int viewportOverlayPadding) {
        config.setViewportOverlayPadding(viewportOverlayPadding);
        resetTouchManager();
        invalidate();
    }

    /**
     * Returns the native aspect ratio of the image.
     *
     * @return The native aspect ratio of the image.
     */
    public float getImageRatio() {
        Bitmap bitmap = getImageBitmap();
        return bitmap != null ? (float) bitmap.getWidth() / (float) bitmap.getHeight() : 0f;
    }

    /**
     * Returns the aspect ratio of the viewport and crop rect.
     *
     * @return The current viewport aspect ratio.
     */
    public float getViewportRatio() {
        return touchManager.getAspectRatio();
    }

    /**
     * Sets the aspect ratio of the viewport and crop rect.  Defaults to
     * the native aspect ratio if <code>ratio == 0</code>.
     *
     * @param ratio The new aspect ratio of the viewport.
     */
    public void setViewportRatio(float ratio) {
        if (Float.compare(ratio, 0) == 0) {
            ratio = getImageRatio();
        }
        touchManager.setAspectRatio(ratio);
        resetTouchManager();
        invalidate();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        @SuppressLint("ResourceType") final Bitmap bitmap = resId > 0
                ? BitmapFactory.decodeResource(getResources(), resId)
                : null;
        setImageBitmap(bitmap);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        final Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmap = bitmapDrawable.getBitmap();
        } else if (drawable != null) {
            bitmap = CropViewUtils.asBitmap(drawable, getWidth(), getHeight());
        } else {
            bitmap = null;
        }

        setImageBitmap(bitmap);
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
//        extensions().load(uri);
    }

    @Override
    public void setImageBitmap(@Nullable Bitmap bitmap) {
        this.bitmap = bitmap;
        resetTouchManager();
        invalidate();
    }

    public void disableTransformation() {
        isTransformationEnabled = false;
    }

    public void drawDummyKeyBoard(boolean isDummyKeyBoardNeeded) {
        this.isDummyKeyBoardNeeded = isDummyKeyBoardNeeded;
    }

    /**
     * @return Current working Bitmap or <code>null</code> if none has been set yet.
     */
    @Nullable
    public Bitmap getImageBitmap() {
        return bitmap;
    }

    private void resetTouchManager() {
        final boolean invalidBitmap = bitmap == null;
        final int bitmapWidth = invalidBitmap ? 0 : bitmap.getWidth();
        final int bitmapHeight = invalidBitmap ? 0 : bitmap.getHeight();
        touchManager.resetFor(bitmapWidth, bitmapHeight, getWidth(), getHeight());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = super.dispatchTouchEvent(event);

        if (!isEnabled() || !isTransformationEnabled) {
            return result;
        }

        touchManager.onEvent(event);
        invalidate();
        return true;
    }

    public void transFormImageForceFully(float scale, float x, float y) {
        touchManager.setScale(scale);
        touchManager.setTouchPoint(x, y);
        touchManager.setLimits();
        invalidate();
    }

    /**
     * Performs synchronous image cropping based on configuration.
     *
     * @return A {@link Bitmap} cropped based on viewport and user panning and zooming or <code>null</code> if no {@link Bitmap} has been
     * provided.
     */
    @Nullable
    public Bitmap crop() {
        if (bitmap == null) {
            return null;
        }

        setDrawingCacheEnabled(true);
        final Bitmap src = getDrawingCache();
        final Bitmap.Config srcConfig = src.getConfig();
//        final Bitmap.Config config = srcConfig == null ? Bitmap.Config.ARGB_8888 : srcConfig;
        final Bitmap.Config config = Bitmap.Config.ARGB_4444;//srcConfig == null ? Bitmap.Config.ARGB_8888 : srcConfig;
        final int viewportHeight = touchManager.getViewportHeight();
        final int viewportWidth = touchManager.getViewportWidth();

        final Bitmap dst = Bitmap.createBitmap(viewportWidth, viewportHeight, config);

        Canvas canvas = new Canvas(dst);
        final int left = (getRight() - viewportWidth) / 2;
        final int top = (getBottom() - viewportHeight) / 2;
        canvas.translate(-left, -top);
        drawBitmap(canvas);
        return dst;
    }

    /**
     * Obtain current viewport width.
     *
     * @return Current viewport width.
     * <p>Note: It might be 0 if layout pass has not been completed.</p>
     */
    public int getViewportWidth() {
        return touchManager.getViewportWidth();
    }

    /**
     * Obtain current viewport height.
     *
     * @return Current viewport height.
     * <p>Note: It might be 0 if layout pass has not been completed.</p>
     */
    public int getViewportHeight() {
        return touchManager.getViewportHeight();
    }

    /**
     * Offers common utility extensions.
     *
     * @return Extensions object used to perform chained calls.
     */
    public Extensions extensions() {
        if (extensions == null) {
            extensions = new Extensions(this);
        }
        return extensions;
    }

    public void setOnImageTransformListener(OnImageTransformListener onImageTransformListener) {
        this.onImageTransformListener = onImageTransformListener;
    }

    /**
     * Optional extensions to perform common actions involving a {@link CropView}
     */
    public static class Extensions {

        private final CropView cropView;
        private OnImageTransformListener onImageTransformListener;

        Extensions(CropView cropView) {
            this.cropView = cropView;
        }

      /*  *//**
         * Load a {@link Bitmap} using an automatically resolved {@link BitmapLoader} which will attempt to scale image to fill view.
         *
         * @param model Model used by {@link BitmapLoader} to load desired {@link Bitmap}
         * @see PicassoBitmapLoader
         * @see GlideBitmapLoader
         *//*
        public void load(@Nullable Object model) {
            new LoadRequest(cropView)
                    .load(model);
        }

        *//**
         * Load a {@link Bitmap} using given {@link BitmapLoader}, you must call {@link LoadRequest#load(Object)} afterwards.
         *
         * @param bitmapLoader {@link BitmapLoader} used to load desired {@link Bitmap}
         * @see PicassoBitmapLoader
         * @see GlideBitmapLoader
         *//*
        public LoadRequest using(@Nullable BitmapLoader bitmapLoader) {
            return new LoadRequest(cropView).using(bitmapLoader);
        }

        *//**
         * Perform an asynchronous crop request.
         *
         * @return {@link CropRequest} used to chain a configure cropping request, you must call either one of:
         * <ul>
         * <li>{@link CropRequest#into(File)}</li>
         * <li>{@link CropRequest#into(OutputStream, boolean)}</li>
         * </ul>
         *//*
        public CropRequest crop() {
            return new CropRequest(cropView);
        }*/

        /**
         * Perform a pick image request using {@link Activity#startActivityForResult(Intent, int)}.
         */
        public void pickUsing(@NonNull Activity activity, int requestCode) {
            CropViewExtensions.pickUsing(activity, requestCode);
        }

        /**
         * Perform a pick image request using {@link Fragment#startActivityForResult(Intent, int)}.
         */
        public void pickUsing(@NonNull Fragment fragment, int requestCode) {
            CropViewExtensions.pickUsing(fragment, requestCode);
        }
    }

    public interface OnImageTransformListener {
        void onImageTransform(Bundle bundle);
    }
}
