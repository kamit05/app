package com.bobbletheme.model;


public class ThemeCategories {

    public Themes[] themes;

    private String displayExpandedView;

    private String numPreviewThemes;

    private String themeCategoryId;

    private String themeCategoryName;

    public Themes[] getThemes ()
    {
        return themes;
    }

    public void setThemes (Themes[] themes)
    {
        this.themes = themes;
    }

    public String getDisplayExpandedView ()
    {
        return displayExpandedView;
    }

    public void setDisplayExpandedView (String displayExpandedView)
    {
        this.displayExpandedView = displayExpandedView;
    }

    public String getNumPreviewThemes ()
    {
        return numPreviewThemes;
    }

    public void setNumPreviewThemes (String numPreviewThemes)
    {
        this.numPreviewThemes = numPreviewThemes;
    }

    public String getThemeCategoryId ()
    {
        return themeCategoryId;
    }

    public void setThemeCategoryId (String themeCategoryId)
    {
        this.themeCategoryId = themeCategoryId;
    }

    public String getThemeCategoryName ()
    {
        return themeCategoryName;
    }

    public void setThemeCategoryName (String themeCategoryName)
    {
        this.themeCategoryName = themeCategoryName;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [themes = "+themes+", displayExpandedView = "+displayExpandedView+", numPreviewThemes = "+numPreviewThemes+", themeCategoryId = "+themeCategoryId+", themeCategoryName = "+themeCategoryName+"]";
    }
}
			
			