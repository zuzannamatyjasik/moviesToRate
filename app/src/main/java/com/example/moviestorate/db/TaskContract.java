package com.example.moviestorate.db;

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.example.moviestorate.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "film";

        public static final String COL_MOVIE_TITLE = "tytul";
        public static final String COL_MOVIE_RATE = "ocena";
        public static final String COL_MOVIE_DESCREPTION = "opis";
        public static final String COL_MOVIE_WATCHED = "obejrzane";
    }
}