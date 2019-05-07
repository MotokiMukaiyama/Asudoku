package motoki_mukaiyama.asudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AppInstrumentedTest {

    Context mContext;

    // @formatter:off
    private static final String INSERT_TABLE_BOOKS_FIRST_HALF =
            "insert into " + BookContract.Books.TABLE_NAME + " ("
                    + BookContract.Books.COL_ID
                    + ","   + BookContract.Books.COL_TITLE
                    + ","   + BookContract.Books.COL_AUTHOR
                    + ","   + BookContract.Books.COL_CATEGORY
                    + ","   + BookContract.Books.COL_EVALUATION
                    + ","   + BookContract.Books.COL_CREATED_DATE
                    + ","   + BookContract.Books.COL_UPDATED_DATE
                    + ") values ";
            ;
    private static final String INSERT_TABLE_MEMOS_FIRST_HALF =
            "insert into " + MemoContract.Memos.TABLE_NAME + " ("
                    +         MemoContract.Memos.COL_BOOK_ID
                    + ","   + MemoContract.Memos.COL_TYPE
                    + ","   + MemoContract.Memos.COL_SORT_ORDER
                    + ","   + MemoContract.Memos.COL_CLEAR
                    + ","   + MemoContract.Memos.COL_CONTENT
                    + ") values ";
    private static final String DELETE_TABLE_BOOKS = "delete from " + BookContract.Books.TABLE_NAME;
    private static final String DELETE_TABLE_MEMOS = "delete from " + MemoContract.Memos.TABLE_NAME;

    private static final String TEST_DB_NAME = "test_asudoku.db";
    private static final int TEST_DB_VERSION = 1;
    // @formatter:on

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void before() {

        //test用context
        mContext = InstrumentationRegistry.getTargetContext();

        //？
        mActivityTestRule.launchActivity(null);

        //test用のDBを設定する
        PostOpenHelper.setmDbName(TEST_DB_NAME);
        PostOpenHelper.setmDbVersion(TEST_DB_VERSION);
    }

    @After
    public void after() {
    }

    @Test
    public void useAppContext() {

        assertEquals("motoki_mukaiyama.asudoku", mContext.getPackageName());

        testCreate();
    }

    private void testCreate() {

        //テストデータ
        final String INSERT_TABLE_BOOKS = INSERT_TABLE_BOOKS_FIRST_HALF
                + "(0, 'title0', 'author0', 'category0', 1, '2019/01/01', '2019/01/01')";
        final String INSERT_TABLE_MEMOS = INSERT_TABLE_MEMOS_FIRST_HALF
                + "(0, 0, 0, 0, 'acquisition0'), "
                + "(0, 0, 1, 0, 'acquisition1'), "
                + "(0, 0, 2, 0, 'acquisition2'), "
                + "(0, 1, 0, 0, 'action0'), "
                + "(0, 1, 1, 0, 'action1'), "
                + "(0, 1, 2, 0, 'action2')  ";

        //DB初期化
        SQLiteDatabase db = new PostOpenHelper(mContext).getWritableDatabase();
        db.execSQL(DELETE_TABLE_BOOKS);
        db.execSQL(DELETE_TABLE_MEMOS);
        db.execSQL(INSERT_TABLE_BOOKS);
        db.execSQL(INSERT_TABLE_MEMOS);
        db.close();

        //作成ボタンクリック
        onView(withId(R.id.actionBarAddButton)).perform(click());

        //EditTextに入力
        onView(withId(R.id.titleCreate)).perform(typeText("title1"));

        //保存ボタンクリック
        onView(withId(R.id.actionBarSaveButton)).perform(click());

        //DBに保存されたかチェック
        db = new PostOpenHelper(mContext).getWritableDatabase();
        Cursor c1 = db.rawQuery("select * from books;", null);
        Cursor c2 = db.rawQuery("select * from memos;", null);
        assertEquals(2,  c1.getCount());
        assertEquals(12, c2.getCount());
        db.close();

        //作成したメモがindexに表示されているかチェック

//        DataInteraction t1 = onData(withId(R.id.title));
////        DataInteraction t2 = t1.inAdapterView(withId(R.id.postsListView));
//        t1.atPosition(0).check(matches(withText("title1")));

//        onData(anything()).inAdapterView(withContentDescription("desc")).atPosition(x).perform(click())
//        onData(anything()).inAdapterView(withId(R.id.postsListView)).atPosition(0).perform(click());

//        ViewInteraction a = onView(FirstViewMatcher.firstView());
//        a.check(matches(withText("title1")));

//        onView(allOf(withId(R.id.postsListView), isDescendantOfA(withId(R.id.title)))).check(matches(withText("title1")));
//        onData(anything())
//                .withParent(withId(R.id.postsListView))
//                .inAdapterView(withId(R.id.postsListView))
//                .atPosition(0)
//                .onChildView(withId(R.id.title))
//                .check(matches(withText("title1")));

        onData(checkTitleText("title0")).check(matches(isDisplayed()));
        onData(checkTitleText("title1")).check(matches(isDisplayed()));
    }

    //TODO title以外も同じように比較したい。簡潔に書けないか？ custom adapterのベストプラクティスを探す
    public static Matcher<Object> checkTitleText(final String titleText) {

        return new BoundedMatcher<Object, Post>(Post.class) {

            @Override
            public boolean matchesSafely(Post post) {
                // 今回はテキストだけを比較、ここで色々Adapter内のデータとあっているか比較する
                return titleText.equals(post.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with content '" + titleText + "'");
            }
        };
    }

}

