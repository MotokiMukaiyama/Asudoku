package motoki_mukaiyama.asudoku;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Post {

    private Integer id;
    private String title;
    private String author;
    private String category;
    private Bitmap image;
    private Integer evaluation;
    private String createdDate;
    private String updatedDate;
    private String acquisition1; //TODO 消す
    private String acquisition2; //TODO 消す
    private String acquisition3; //TODO 消す
    private String action1; //TODO 消す
    private String action2; //TODO 消す
    private String action3; //TODO 消す
    private String thoughts; //TODO 消す
    private List<String> acquisitions;
    private List<String> actions;

    static final int NUM_OF_ACQUISITIONS = 3;
    static final int NUM_OF_ACTIONS = 3;

    public Post(){
        // @formatter:off
        this.acquisitions = new ArrayList<>();
        this.actions      = new ArrayList<>();
        for (int i=0; i<NUM_OF_ACQUISITIONS; ++i) this.acquisitions.add(new String());
        for (int i=0; i<NUM_OF_ACTIONS;      ++i) this.actions     .add(new String());
        // @formatter:on
    }

    // @formatter:off
    public Integer      getId           () { return id;           }
    public String       getAcquisition1 () { return acquisition1; }
    public String       getAcquisition2 () { return acquisition2; }
    public String       getAcquisition3 () { return acquisition3; }
    public String       getAction1      () { return action1;      }
    public String       getAction2      () { return action2;      }
    public String       getAction3      () { return action3;      }
    public String       getTitle        () { return title;        }
    public String       getAuthor       () { return author;       }
    public String       getCategory     () { return category;     }
    public Bitmap       getImage        () { return image;        }
    public Integer      getEvaluation   () { return evaluation;   }
    public String       getCreatedDate  () { return createdDate;  }
    public String       getUpdatedDate  () { return updatedDate;  }
    public String       getThoughts     () { return thoughts;     }
    public List<String> getAcquisitions () { return Collections.unmodifiableList(acquisitions);} //予期せぬ変更を防ぐため変更不可能な参照を返す
    public List<String> getActions      () { return Collections.unmodifiableList(actions);     } //予期せぬ変更を防ぐため変更不可能な参照を返す

    public void setId           ( Integer id           ) { this.id           = id;                     }
    public void setAcquisition1 ( String  acquisition1 ) { this.acquisition1 = acquisition1;           }
    public void setAcquisition2 ( String  acquisition2 ) { this.acquisition2 = acquisition2;           }
    public void setAcquisition3 ( String  acquisition3 ) { this.acquisition3 = acquisition3;           }
    public void setAction1      ( String  action1      ) { this.action1      = action1;                }
    public void setAction2      ( String  action2      ) { this.action2      = action2;                }
    public void setAction3      ( String  action3      ) { this.action3      = action3;                }
    public void setTitle        ( String  title        ) { this.title        = title;                  }
    public void setAuthor       ( String  author       ) { this.author       = author;                 }
    public void setCategory     ( String  category     ) { this.category     = category;               }
    public void setImage        ( Bitmap  image        ) { this.image        = image;                  }
    public void setEvaluation   ( Integer evaluation   ) { this.evaluation   = evaluation;             }
    public void setCreatedDate  ( String  createdDate  ) { this.createdDate  = createdDate;            }
    public void setUpdatedDate  ( String  updatedDate  ) { this.updatedDate  = updatedDate;            }
    public void setThoughts     ( String  thoughts     ) { this.thoughts     = thoughts;               }
    public void setEvaluation   ( String  evaluation   ) { setEvaluation(Integer.valueOf(evaluation)); }

    public void setAcquisition(int index, String element){acquisitions.set(index, element);}
    public void setAction     (int index, String element){actions     .set(index, element);}
    // @formatter:on


    //Listの要素があればその要素を、なければ第三引数を返す
    private String getListElem(List<String> list, int index, String repStr) {
        if (list.size() > index) return list.get(index);
        else return repStr;
    }
    public String getAcquisition(int index, String repStr) {
        return getListElem(this.acquisitions, index, repStr);
    }
    public String getAction(int index, String repStr) {
        return getListElem(this.actions, index, repStr);
    }

    //Listの要素が空文字か、そもそも要素がない場合はtrueを返す
    private boolean isEmptyListElem(List<String> list, int index) {
        if (list.size() > index && list.get(index).isEmpty()) return true;
        else if (list.size() > index && !list.get(index).isEmpty()) return false;
        else return true;
    }
    public boolean isEmptyAcquisition(int index) {
        return isEmptyListElem(this.acquisitions, index);
    }
    public boolean isEmptyAction(int index) {
        return isEmptyListElem(this.actions, index);
    }

    //postIdのレコードをpostで更新する
    void updateDB(Context context, boolean inserts) {

        //TODO 2テーブル更新するのでトランザクションを設定する
        //TODO 値の設定はinsertとupdateで共通化していいけど、DB更新処理は分ける（forループも関数に含める）

        //DB open
        PostOpenHelper postOpenHelper = new PostOpenHelper(context);
        SQLiteDatabase db = postOpenHelper.getWritableDatabase();

        final int NUM_OF_TYPES = 2;
        final int NUM_OF_SORT_ORDERS = 3;
        long bookId = inserts? -1 : getId();

        //EditTextの値を取得
        ContentValues book = new ContentValues();
        ContentValues[][] memos;

        //Book
        // @formatter:off
        //更新する値を設定
        if(  inserts) book.put(BookContract.Books.COL_CREATED_DATE, getCreatedDate()  );
        if(! inserts) book.put(BookContract.Books.COL_ID,           bookId            );
                      book.put(BookContract.Books.COL_TITLE,        getTitle()        );
                      book.put(BookContract.Books.COL_AUTHOR,       getAuthor()       );
                      book.put(BookContract.Books.COL_CATEGORY,     getCategory()     );
                      book.put(BookContract.Books.COL_EVALUATION,   getEvaluation()   );
                      book.put(BookContract.Books.COL_UPDATED_DATE, getUpdatedDate()  );
        //DB更新
        if(inserts) bookId = insertBook(db, book); //insert時に採番したIDを取得
        else                 updateBook(db, book);
        // @formatter:on

        //Memos
        //TODO MemoのViewを可変にしたらここも可変にする
        memos = new ContentValues[NUM_OF_TYPES][NUM_OF_SORT_ORDERS];
        for (int i = 0; i < NUM_OF_TYPES; ++i) {

            for (int j = 0; j < NUM_OF_SORT_ORDERS; ++j) {

                memos[i][j] = new ContentValues();

                //更新する値を設定
                // @formatter:off
                memos[i][j].put(MemoContract.Memos.COL_BOOK_ID    , bookId                      );
                memos[i][j].put(MemoContract.Memos.COL_TYPE       , i                           );
                memos[i][j].put(MemoContract.Memos.COL_SORT_ORDER , j                           );
                memos[i][j].put(MemoContract.Memos.COL_CLEAR      , 0                           );
                if(i==0) memos[i][j].put(MemoContract.Memos.COL_CONTENT    , getAcquisition(j , "") );
                else     memos[i][j].put(MemoContract.Memos.COL_CONTENT    , getAction     (j , "") );

                //DB更新
                if(inserts) insertMemos(db, memos, i, j);
                else        updateMemos(db, memos, i, j);
                // @formatter:on
            }
        }

        //DB close
        db.close();
    }

    //Bookテーブルをinsert
    private long insertBook(SQLiteDatabase db, ContentValues book){
        return db.insert(
                BookContract.Books.TABLE_NAME,
                null,
                book
        );
    }
    //Bookテーブルをupdate
    private int updateBook(SQLiteDatabase db, ContentValues book){
        return db.update(
                BookContract.Books.TABLE_NAME,
                book,
                BookContract.Books.COL_ID + " = ?",
                new String[]{Integer.toString(getId())}
        );
    }
    //Memoテーブルをinsert
    private long insertMemos(SQLiteDatabase db, ContentValues[][] memos, int i, int j){
        // @formatter:off
        return db.insert(
                MemoContract.Memos.TABLE_NAME,
                null,
                memos[i][j]
        );
        // @formatter:on
    }
    //Memoテーブルをupdate
    private int updateMemos(SQLiteDatabase db, ContentValues[][] memos, int i, int j){
        // @formatter:off
        return db.update(
                MemoContract.Memos.TABLE_NAME,
                memos[i][j],
                MemoContract.Memos.COL_BOOK_ID       + " = ? and "
                           + MemoContract.Memos.COL_TYPE          + " = ? and "
                           + MemoContract.Memos.COL_SORT_ORDER    + " = ?",
                new String[]{
                        Integer.toString(getId()),
                        Integer.toString(i),
                        Integer.toString(j)
                }
        );
        // @formatter:on
    }

    //投稿を削除する
    public void deleteDB(Context context) {

        //DB open
        PostOpenHelper postOpenHelper = new PostOpenHelper(context);
        SQLiteDatabase db = postOpenHelper.getWritableDatabase();

        //delete Books table
        db.delete(
                BookContract.Books.TABLE_NAME,
                BookContract.Books.COL_ID + " = ?",
                new String[]{Integer.toString(getId())}
        );

        //delete Memos table
        db.delete(
                MemoContract.Memos.TABLE_NAME,
                MemoContract.Memos.COL_BOOK_ID + " = ?",
                new String[]{Integer.toString(getId())}
        );

        //DB close
        db.close();
    }
}
