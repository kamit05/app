package com.bobbletheme.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bobbletheme.R;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

public class ThemeUtils {

    private static final String TAG = ThemeUtils.class.getSimpleName();
    private static final HashMap<String, String> sDeviceOverrideValueMap = new HashMap<>();
    private static final HashMap<String, String> sBuildKeyValues;
    private static final String sBuildKeyValuesDebugString;

    static {
        sBuildKeyValues = new HashMap<>();
        final ArrayList<String> keyValuePairs = new ArrayList<>();
        sBuildKeyValuesDebugString = "[" + TextUtils.join(" ", keyValuePairs) + "]";
    }

    /***
     * Method returning default keyboard height
     * @param res
     * @return
     */
    public static int getDefaultKeyboardHeight(final Resources res) {
        final DisplayMetrics dm = res.getDisplayMetrics();
        final String keyboardHeightInDp = getDeviceOverrideValue(
                res, R.array.keyboard_heights, null /* defaultValue */);
        final float keyboardHeight;
        if (TextUtils.isEmpty(keyboardHeightInDp)) {
            keyboardHeight = res.getDimension(R.dimen.config_default_keyboard_height);
        } else {
            keyboardHeight = Float.parseFloat(keyboardHeightInDp) * dm.density;
        }
        final float maxKeyboardHeight = res.getFraction(
                R.fraction.config_max_keyboard_height, dm.heightPixels, dm.heightPixels);
        float minKeyboardHeight = res.getFraction(
                R.fraction.config_min_keyboard_height, dm.heightPixels, dm.heightPixels);
        if (minKeyboardHeight < 0.0f) {
            // Specified fraction was negative, so it should be calculated against display
            // width.
            minKeyboardHeight = -res.getFraction(
                    R.fraction.config_min_keyboard_height, dm.widthPixels, dm.widthPixels);
        }

        int max = (int) (Float.parseFloat(String.valueOf(maxKeyboardHeight)));
        int min = (int) (Float.parseFloat(String.valueOf(minKeyboardHeight)));
        int normal = min + ((max - min) * 30) / 100;        //30% of max - min

        return normal;
    }

    /***
     * Method getting device overridden value
     * @param res
     * @param overrideResId
     * @param defaultValue
     * @return
     */
    public static String getDeviceOverrideValue(final Resources res, final int overrideResId,
                                                final String defaultValue) {
        final int orientation = res.getConfiguration().orientation;
        final String key = overrideResId + "-" + orientation;
        if (sDeviceOverrideValueMap.containsKey(key)) {
            return sDeviceOverrideValueMap.get(key);
        }

        final String[] overrideArray = res.getStringArray(overrideResId);
        final String overrideValue = findConstantForKeyValuePairs(sBuildKeyValues, overrideArray);
        // The overrideValue might be an empty string.
        if (overrideValue != null) {
            Log.i(TAG, "Find override value:"
                    + " resource=" + res.getResourceEntryName(overrideResId)
                    + " build=" + sBuildKeyValuesDebugString
                    + " override=" + overrideValue);
            sDeviceOverrideValueMap.put(key, overrideValue);
            return overrideValue;
        }

        sDeviceOverrideValueMap.put(key, defaultValue);
        return defaultValue;
    }

    static String findConstantForKeyValuePairs(final HashMap<String, String> keyValuePairs,
                                               final String[] conditionConstantArray) {
        if (conditionConstantArray == null || keyValuePairs == null) {
            return null;
        }
        String foundValue = null;
        for (final String conditionConstant : conditionConstantArray) {
            final int posComma = conditionConstant.indexOf(',');
            if (posComma < 0) {
                Log.w(TAG, "Array element has no comma: " + conditionConstant);
                continue;
            }
            final String condition = conditionConstant.substring(0, posComma);
            if (condition.isEmpty()) {
                Log.w(TAG, "Array element has no condition: " + conditionConstant);
                continue;
            }
            try {
                if (fulfillsCondition(keyValuePairs, condition)) {
                    // Take first match
                    if (foundValue == null) {
                        foundValue = conditionConstant.substring(posComma + 1);
                    }
                    // And continue walking through all conditions.
                }
            } catch (final DeviceOverridePatternSyntaxError e) {
                Log.w(TAG, "Syntax error, ignored", e);
            }
        }
        return foundValue;
    }

    private static boolean fulfillsCondition(final HashMap<String, String> keyValuePairs,
                                             final String condition) throws DeviceOverridePatternSyntaxError {
        final String[] patterns = condition.split(":");
        // Check all patterns in a condition are true
        boolean matchedAll = true;
        for (final String pattern : patterns) {
            final int posEqual = pattern.indexOf('=');
            if (posEqual < 0) {
                throw new DeviceOverridePatternSyntaxError("Pattern has no '='", condition);
            }
            final String key = pattern.substring(0, posEqual);
            final String value = keyValuePairs.get(key);
            if (value == null) {
                throw new DeviceOverridePatternSyntaxError("Unknown key", condition);
            }
            final String patternRegexpValue = pattern.substring(posEqual + 1);
            try {
                if (!value.matches(patternRegexpValue)) {
                    matchedAll = false;
                    // And continue walking through all patterns.
                }
            } catch (final PatternSyntaxException e) {
                throw new DeviceOverridePatternSyntaxError("Syntax error", condition, e);
            }
        }
        return matchedAll;
    }

    @SuppressWarnings("serial")
    static class DeviceOverridePatternSyntaxError extends Exception {
        public DeviceOverridePatternSyntaxError(final String message, final String expression) {
            this(message, expression, null);
        }

        public DeviceOverridePatternSyntaxError(final String message, final String expression,
                                                final Throwable throwable) {
            super(message + ": " + expression, throwable);
        }
    }

    /***
     * Method decoding sample image Bitmap
     * @param pictureUri
     * @param width
     * @param height
     * @param context
     * @return
     */
    public static Bitmap decodeSampledBitmapFromStrem(Uri pictureUri, int width, int height, Context context) {
        // First decode with inJustDecodeBounds=true to check dimensions
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(pictureUri), null, options);
            int sampleSize = calculateInSampleSize(options, width, height);

            options.inSampleSize = sampleSize;

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(pictureUri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * Methopd calculating sample image size
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    || (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /***
     * Method fetching device resolution
     * @param context
     * @return
     */
    public static String getScreenResolution( Context context){
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


}
