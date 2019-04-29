package motoki_mukaiyama.asudoku;

import android.provider.BaseColumns;

public class PostContract {
    public PostContract(){};
    public static abstract class Posts implements BaseColumns{
        public static final String TABLE_NAME = "posts";
        public static final String COL_ID = "_id";
        public static final String COL_TITLE = "title";
        public static final String COL_AUTHOR = "author";
        public static final String COL_CATEGORY = "category";
        public static final String COL_IMAGE = "image";
        public static final String COL_EVALUATION = "evaluation";
        public static final String COL_CREATED_DATE = "created_date";
        public static final String COL_UPDATED_DATE = "updated_date";
        public static final String COL_ACQUISITION1 = "acquisition1";
        public static final String COL_ACQUISITION2 = "acquisition2";
        public static final String COL_ACQUISITION3 = "acquisition3";
        public static final String COL_ACTION1 = "action1";
        public static final String COL_ACTION2 = "action2";
        public static final String COL_ACTION3 = "action3";
        public static final String COL_THOUGHT = "thought";
    }
}
