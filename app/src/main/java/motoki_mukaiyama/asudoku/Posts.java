package motoki_mukaiyama.asudoku;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Posts {

    private List<Post> posts = new ArrayList<>();

    public List<Post> get() {
        return Collections.unmodifiableList(posts); //予期せぬ変更を防ぐため変更不可能な参照を返す
    }

    public Post get(int index) {
        return posts.get(index);
    }

    //DBアクセスしてPostsをセットする
    public int setFromDB(Context context) {
        return setFromDB(context, -1);
    }

    //DBアクセスしてPostsをセットする
    //引数でwhereで使用するidを指定
    public int setFromDB(Context context, int whereId) {

        // DB open
        PostOpenHelper postOpenHelper = new PostOpenHelper(context);
        SQLiteDatabase db = postOpenHelper.getWritableDatabase();

        // @formatter:off
        //WHERE句の条件
        final String CONDITION;
        if(whereId > -1) CONDITION = BookContract.Books.COL_ID + " = " + whereId;
        else             CONDITION = "1 = 1";

        // SELECT
        final String SQL =
                "select "
                        +        BookContract.Books.COL_ID
                        + ", " + BookContract.Books.COL_TITLE
                        + ", " + BookContract.Books.COL_AUTHOR
                        + ", " + BookContract.Books.COL_CATEGORY
                        + ", " + BookContract.Books.COL_EVALUATION
                        + ", " + BookContract.Books.COL_CREATED_DATE
                        + ", " + BookContract.Books.COL_UPDATED_DATE
                        + ", " + MemoContract.Memos.COL_TYPE
                        + ", " + MemoContract.Memos.COL_CONTENT
                        + ", " + MemoContract.Memos.COL_CLEAR
                + " from "
                        + BookContract.Books.TABLE_NAME
                        + " inner join "
                                + MemoContract.Memos.TABLE_NAME
                        + " on "
                                +         BookContract.Books.COL_ID
                                + " = " + MemoContract.Memos.COL_BOOK_ID
                + " where "
                        + CONDITION
                + " order by "
                        +        BookContract.Books.COL_ID + " desc"
                        + ", " + MemoContract.Memos.COL_TYPE
                        + ", " + MemoContract.Memos.COL_SORT_ORDER
                + ";";
        // @formatter:on
        Cursor c = db.rawQuery(SQL, null);

        // postsに詰める
//        ArrayList<Post> posts = new ArrayList<>();
        int pre_id = -1;
        int bookCnt = -1;
        int acquisitionCnt = 0;
        int actionCnt = 0;
        while (c.moveToNext()) {

            // Book.idが新しいときはBookの情報をセットする（cはBook.idでソートされている前提）
            int id = c.getInt(c.getColumnIndex(BookContract.Books.COL_ID));
            if (id != pre_id) {
                Post post = new Post();
                // @formatter:off
//                post.setImage       (BitmapFactory.decodeResource(getResources(), R.drawable.book_image_sample2)); //TODO Imageを持つのではなく、Imageのパスを文字列で持つようにする
                post.setId          (c.getInt   (c.getColumnIndex(BookContract.Books.COL_ID)));
                post.setTitle       (c.getString(c.getColumnIndex(BookContract.Books.COL_TITLE)));
                post.setAuthor      (c.getString(c.getColumnIndex(BookContract.Books.COL_AUTHOR)));
                post.setCategory    (c.getString(c.getColumnIndex(BookContract.Books.COL_CATEGORY)));
                post.setEvaluation  (c.getInt   (c.getColumnIndex(BookContract.Books.COL_EVALUATION)));
                post.setCreatedDate (c.getString(c.getColumnIndex(BookContract.Books.COL_CREATED_DATE)));
                post.setUpdatedDate (c.getString(c.getColumnIndex(BookContract.Books.COL_UPDATED_DATE)));
                // @formatter:on
                this.posts.add(post);
                pre_id = id;
                ++bookCnt;
                acquisitionCnt = 0;
                actionCnt = 0;
            }

            // Memoの情報をセットする
            int type = c.getInt(c.getColumnIndex(MemoContract.Memos.COL_TYPE));
            // @formatter:off
            if (type == 0) this.posts.get(bookCnt).setAcquisition(acquisitionCnt++, c.getString(c.getColumnIndex(MemoContract.Memos.COL_CONTENT)));
            else           this.posts.get(bookCnt).setAction     (actionCnt++,      c.getString(c.getColumnIndex(MemoContract.Memos.COL_CONTENT)));
            // @formatter:on
        }

        // DB close
        c.close();
        db.close();

        return bookCnt; //addした数を返す
    }
}
