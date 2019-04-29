package motoki_mukaiyama.asudoku;

import android.provider.BaseColumns;

public class BookContract {
    public BookContract(){};
    public static abstract class Books implements BaseColumns{
        // @formatter:off
        public static final String TABLE_NAME       = "books";
        public static final String COL_ID           = "_id";
        public static final String COL_TITLE        = "title";
        public static final String COL_AUTHOR       = "author";
        public static final String COL_CATEGORY     = "category";
        public static final String COL_IMAGE        = "image";
        public static final String COL_EVALUATION   = "evaluation";
        public static final String COL_CREATED_DATE = "created_date";
        public static final String COL_UPDATED_DATE = "updated_date";
        // @formatter:on
    }
}
