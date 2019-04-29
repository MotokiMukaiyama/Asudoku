package motoki_mukaiyama.asudoku;

import android.provider.BaseColumns;

public class MemoContract {
    public MemoContract(){};
    public static abstract class Memos implements BaseColumns{
        // @formatter:off
        public static final String TABLE_NAME       = "memos";
        public static final String COL_BOOK_ID      = "book_id";
        public static final String COL_TYPE         = "type";
        public static final String COL_SORT_ORDER   = "sort_order";
        public static final String COL_CONTENT      = "content";
        public static final String COL_CLEAR        = "clear";
        // @formatter:on
    }
}
