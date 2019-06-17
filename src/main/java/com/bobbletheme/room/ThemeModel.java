package com.bobbletheme.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = ThemeDatabase.TABLE_NAME_THEME)
public class ThemeModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int themeId;

    public String isLightTheme;

    public String hintLetterColor;

    public String themeBackgroundImageHDPIURL;

    public String keyBorderRadius;

    public String keyBackgroundColor;

    public String suggestionsColorValidTypedWord;

    public String themeBackgroundImageXXHDPIURL;

    public String swipeGestureTrailColor;

    public String moreSuggestionsButtonBackgroundColor;

    public String suggestionsColorSuggested;

    public String themePreviewImageHDPIURL;

    public String gestureFloatingPreviewTextColor;

    public String functionalTextColor;

    public String suggestionsBarPageIndicatorColor;

    public String suggestionsColorAutoCorrect;

    public String themePreviewImageORIGINALURL;

    public String keyPopUpPreviewBackgroundColor;

    public String feedbackBarPopupBackgroundColor;

    public String keyboardBackgroundColor;

    public String themeBackgroundImageXHDPIURL;

    public String themePreviewImageXHDPIURL;

    public String themePreviewImageXXHDPIURL;

    public String keyTextColor;

    public String themeType;

    public String themeBackgroundImageORIGINALURL;

    public String moreSuggestionsPanelBackgroundColor;

    public String themeName;

    public String emojiRowBackgroundColor;

    public String suggestionsColorTypedWord;

    public String keyVerticalGap;

    public String enterKeyBorderRadius;

    public String hintLabelColor;

    public String suggestionsBarIconSelectedColor;

    public String showNonAlphaKeyBorder;

    public String bobbleBar;

    public String enterKeyCircleBackgroundColor;

    public String gestureFloatingPreviewColor;

    public String keyboardBackgroundOpacity;

    public String suggestionsBarBackgroundColor;

}
