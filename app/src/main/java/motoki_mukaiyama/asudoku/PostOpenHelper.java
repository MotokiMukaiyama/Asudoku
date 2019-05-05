package motoki_mukaiyama.asudoku;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PostOpenHelper extends SQLiteOpenHelper {

    //デフォルトのDB設定（アプリ用）
    //テストの場合は別のDBを設定する
    private static String mDbName = "asudoku.db";
    private static int mDbVersion = 33;

    // @formatter:off
    private static final String CREATE_TABLE_BOOKS =
    "create table " +
            BookContract.Books.TABLE_NAME                  + " (" +
                    BookContract.Books.COL_ID			   + " integer primary key autoincrement not null," +
                    BookContract.Books.COL_TITLE		   + " text not null," +
                    BookContract.Books.COL_AUTHOR		   + " text," +
                    BookContract.Books.COL_CATEGORY		   + " text," +
                    BookContract.Books.COL_IMAGE		   + " text," +
                    BookContract.Books.COL_EVALUATION	   + " integer, " +
                    BookContract.Books.COL_CREATED_DATE	   + " text not null," +
                    BookContract.Books.COL_UPDATED_DATE	   + " text not null " +
            ")";
    private static final String CREATE_TABLE_MEMOS =
    "create table " +
            MemoContract.Memos.TABLE_NAME                  + " (" +
                    MemoContract.Memos.COL_BOOK_ID         + " integer not null," +
                    MemoContract.Memos.COL_TYPE            + " integer not null," +
                    MemoContract.Memos.COL_SORT_ORDER      + " integer not null," +
                    MemoContract.Memos.COL_CONTENT         + " text," +
                    MemoContract.Memos.COL_CLEAR           + " integer not null," +
                    "PRIMARY KEY (" +
                        MemoContract.Memos.COL_BOOK_ID         + ", " +
                        MemoContract.Memos.COL_TYPE            + ", " +
                        MemoContract.Memos.COL_SORT_ORDER      +
                    ")" +
            ")";

    //    TODO 外部データ化する
    private static final String INIT_TABLE_BOOKS =
            "insert into " + BookContract.Books.TABLE_NAME + " ("
                            + BookContract.Books.COL_ID
                    + ","   + BookContract.Books.COL_TITLE
                    + ","   + BookContract.Books.COL_AUTHOR
                    + ","   + BookContract.Books.COL_CATEGORY
                    + ","   + BookContract.Books.COL_EVALUATION
                    + ","   + BookContract.Books.COL_CREATED_DATE
                    + ","   + BookContract.Books.COL_UPDATED_DATE
            + ") values "

                    //デフォルト
                    + "(1, '（サンプル）アウトプット大全', '樺沢紫苑', '自己啓発', 5, '2019/01/01', '2019/01/01'), "
                    + "(0, 'あす読の使い方', '著者○○', 'ジャンル○○', 3, '2019/02/02', '2019/02/02')"

            //スクショ用
//                    + "(3, 'アウトプット大全', '樺沢紫苑', '自己啓発', 5, '2019/04/26', '2019/04/26'), "
//                    + "(2, '夢をかなえるゾウ', '水野敬也', '自己啓発', 5, '2019/04/15', '2019/04/15'), "
//                    + "(1, 'ブルーオーシャン戦略', 'W・チャン・キム、ネレ・モボルニュ', 'ビジネス', 4, '2019/03/24', '2019/03/24'), "
//                    + "(0, '嫌われる勇気', '岸見一郎、古賀史健', '自己啓発', 4, '2019/02/26', '2019/02/26')"
            ;
    private static final String INIT_TABLE_MEMOS =
            "insert into " + MemoContract.Memos.TABLE_NAME + " ("
                    +         MemoContract.Memos.COL_BOOK_ID
                    + ","   + MemoContract.Memos.COL_TYPE
                    + ","   + MemoContract.Memos.COL_SORT_ORDER
                    + ","   + MemoContract.Memos.COL_CLEAR
                    + ","   + MemoContract.Memos.COL_CONTENT
            + ") values "

                    //デフォルト
                    + "(1, 0, 0, 0, 'インプットとアウトプットの比率は3:7が理想'), "
                    + "(1, 0, 1, 0, 'アウトプットがあるからインプットも捗る'), "
                    + "(1, 0, 2, 0, 'アウトプットすることでしか人は変われない'), "
                    + "(1, 1, 0, 0, ''), "
                    + "(1, 1, 1, 0, 'Amazonで漫画のレビューを書く'), "
                    + "(1, 1, 2, 0, 'なんでもいいので1ツイートしてみる'), "
                    + "(0, 0, 0, 0, '【作成方法】\n左上の＋をタップでメモ作成画面に移動します。'), "
                    + "(0, 0, 1, 0, '【編集方法】\nメモをタップでメモ編集画面に移動します。'), "
                    + "(0, 0, 2, 0, '【気づき】\n左の円マークが「気付き」を表します。\n本で学んだこと、自分で考えたことを忘れないようにメモしましょう。'), "
                    + "(0, 1, 0, 0, '【アクション】\n左の人マークが「アクション」を表します。\n自分の生活に読んだことを活かすためにアクションを起こしましょう。'), "
                    + "(0, 1, 1, 0, '【評価】\n星の数で本を評価することで後で自分にとって重要な本を探せます。'), "
                    + "(0, 1, 2, 0, '【ヒント】\n十分に身に付いた気づきや実行したアクションは編集画面から削除して、重要なことだけを毎日効率的に見返しましょう。')"

            //スクショ用
//                    + "(3, 0, 0, 0, 'インプットとアウトプットの比率は3:7'), "
//                    + "(3, 0, 1, 0, ''), "
//                    + "(3, 0, 2, 0, ''), "
//                    + "(3, 1, 0, 0, ''), "
//                    + "(3, 1, 1, 0, 'Amazonで漫画のレビューを書く'), "
//                    + "(3, 1, 2, 0, 'なんでもいいので1ツイートしてみる'), "
//                    + "(2, 0, 0, 0, '人生は行動しないと変わらない'), "
//                    + "(2, 0, 1, 0, ''), "
//                    + "(2, 0, 2, 0, ''), "
//                    + "(2, 1, 0, 0, '仕事から帰ったら靴を磨く'), "
//                    + "(2, 1, 1, 0, 'コンビニで1円以上の募金をする'), "
//                    + "(2, 1, 2, 0, ''), "
//                    + "(1, 0, 0, 0, 'バリュー・カーブでメリハリをつけ、差別化とコスト削減'), "
//                    + "(1, 0, 1, 0, '競争は目的ではなく手段'), "
//                    + "(1, 0, 2, 0, ''), "
//                    + "(1, 1, 0, 0, ''), "
//                    + "(1, 1, 1, 0, ''), "
//                    + "(1, 1, 2, 0, '明日の予定を立てる'), "
//                    + "(0, 0, 0, 0, '考え方には原因論と目的論がある'), "
//                    + "(0, 0, 1, 0, '大切なのは何を持っているかではなく、持っているものをどう使うか'), "
//                    + "(0, 0, 2, 0, ''), "
//                    + "(0, 1, 0, 0, ''), "
//                    + "(0, 1, 1, 0, ''), "
//                    + "(0, 1, 2, 0, '')"
            ;
    private static final String DROP_TABLE_BOOKS = "drop table if exists " + BookContract.Books.TABLE_NAME;
    private static final String DROP_TABLE_MEMOS = "drop table if exists " + MemoContract.Memos.TABLE_NAME;
    // @formatter:on

//    public PostOpenHelper(Context context, String dbName, int dbVersion) {
//        super(context, dbName, null, dbVersion);
//    }
//    引数なしの場合は本番用のDB名とバージョンを使用する
    public PostOpenHelper(Context context) {
        super(context, mDbName, null, mDbVersion);
    }

    public static void setmDbName(String mDbName){
        PostOpenHelper.mDbName = mDbName;
    }
    public static void setmDbVersion(int mDbVersion){
        PostOpenHelper.mDbVersion = mDbVersion;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOKS);
        db.execSQL(CREATE_TABLE_MEMOS);
        db.execSQL(INIT_TABLE_BOOKS);
        db.execSQL(INIT_TABLE_MEMOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_BOOKS);
        db.execSQL(DROP_TABLE_MEMOS);
        onCreate(db);
    }
}
