package com.bobbletheme.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    long insertTheme(ThemeModel themeModel);

    @Insert
    void insertThemeList(List<ThemeModel> themeList);

    @Query("SELECT * FROM " + ThemeDatabase.TABLE_NAME_THEME)
    List<ThemeModel> fetchAllThemes();

    @Query("SELECT * FROM " + ThemeDatabase.TABLE_NAME_THEME + " WHERE themeType = :themeType")
    List<ThemeModel> fetchThemeListByCategory(String themeType);

    @Query("SELECT * FROM " + ThemeDatabase.TABLE_NAME_THEME + " WHERE themeId = :themeId")
    ThemeModel fetchThemeListById(int themeId);

    @Update
    int updateTheme(ThemeModel themeModel);

    @Delete
    int deleteTheme(ThemeModel themeModel);
}
