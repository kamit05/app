
package com.bobbletheme.model;

public class ThemeVariation {

    private ThemeCategories[] themeCategories;

    private DefaultThemeProperties defaultThemeProperties;

    public ThemeCategories[] getThemeCategories ()
    {
        return themeCategories;
    }

    public void setThemeCategories (ThemeCategories[] themeCategories)
    {
        this.themeCategories = themeCategories;
    }

    public DefaultThemeProperties getDefaultThemeProperties ()
    {
        return defaultThemeProperties;
    }

    public void setDefaultThemeProperties (DefaultThemeProperties defaultThemeProperties)
    {
        this.defaultThemeProperties = defaultThemeProperties;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [themeCategories = "+themeCategories+", defaultThemeProperties = "+defaultThemeProperties+"]";
    }
}
