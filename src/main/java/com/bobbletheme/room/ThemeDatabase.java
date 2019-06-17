package com.bobbletheme.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ThemeModel.class}, version = 1, exportSchema = false)
public abstract class ThemeDatabase extends RoomDatabase {

    public static final String DB_NAME = "theme_db";
    public static final String TABLE_NAME_THEME = "theme";

    public abstract DaoAccess daoAccess();

}
