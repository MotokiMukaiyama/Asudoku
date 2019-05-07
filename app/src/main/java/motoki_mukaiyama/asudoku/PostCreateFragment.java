package motoki_mukaiyama.asudoku;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static motoki_mukaiyama.asudoku.PostEditFragment.setEvaluationImage;

public class PostCreateFragment extends Fragment {

    private int evaluation = 5;
    private Handler handler;

    public void PostCreate() {
    }

    public static PostCreateFragment newInstance(String isbn) {

        //引数を作成
        Bundle args = new Bundle();
        args.putString(PostIndexFragment.EXTRA_ISBN, isbn);

        //引数を設定して、フラグメントを返す
        PostCreateFragment postCreateFragment = new PostCreateFragment();
        postCreateFragment.setArguments(args);
        return postCreateFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_post_create, container, false);

        getActivity().setTitle(R.string.ActionBarTitleCreate);

        //ツールバーのボタン表示切替
        getActivity().findViewById(R.id.actionBarAddButton).setVisibility(View.GONE);
        getActivity().findViewById(R.id.actionBarSaveButton).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.actionBarRemoveButton).setVisibility(View.GONE);

        //フォーカスのイベント
        view.findViewById(R.id.titleCreate).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                //バリデーション
                validate(view, hasFocus);
            }
        });

        //作成ボタンのイベント
        getActivity().findViewById(R.id.actionBarSaveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Post post = getPostFromEditText();
                String now = new SimpleDateFormat(getString(R.string.dateShowFormat), Locale.JAPAN).format(new Date()); //TODO string
                post.setCreatedDate(now);
                post.setUpdatedDate(now);
                post.updateDB(getActivity(), true);

                //キーボードを閉じる
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                //投稿一覧画面へ遷移
                PostIndexFragment postIndexFragment = PostIndexFragment.newInstance();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, postIndexFragment)
                        .commit();
            }
        });

        //評価の初期画像を設定
        View includeView = view.findViewById(R.id.include_views_evaluation_star_create);
        setEvaluationImage(includeView, evaluation);

        //評価のクリックイベント
        // @formatter:off
        (includeView.findViewById(R.id.evaluationStar1)).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { evaluation = 1; setEvaluationImage((View)view.getParent(), evaluation);}});
        (includeView.findViewById(R.id.evaluationStar2)).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { evaluation = 2; setEvaluationImage((View)view.getParent(), evaluation);}});
        (includeView.findViewById(R.id.evaluationStar3)).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { evaluation = 3; setEvaluationImage((View)view.getParent(), evaluation);}});
        (includeView.findViewById(R.id.evaluationStar4)).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { evaluation = 4; setEvaluationImage((View)view.getParent(), evaluation);}});
        (includeView.findViewById(R.id.evaluationStar5)).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View view) { evaluation = 5; setEvaluationImage((View)view.getParent(), evaluation);}});
        // @formatter:on

        //前画面からISBNを取得
        final String isbn = getArguments().getString(PostIndexFragment.EXTRA_ISBN);

        //GoogleBooksAPIと通信して結果をEditTextにセット
        SetEditTextViaGoogleBooksAPI(isbn, view);

        return view;
    }

    //バリデーション
    void validate(View view, Boolean hasFocus) {
        //フォーカスアウトのとき
        if (!hasFocus) {
            //タイトルが入力されているときだけ追加ボタンを有効にする
            String title = ((EditText) view).getText().toString();
            // @formatter:off
            if(title.isEmpty()) (getActivity().findViewById(R.id.actionBarSaveButton)).setEnabled(false);
            else                (getActivity().findViewById(R.id.actionBarSaveButton)).setEnabled(true);
            // @formatter:on
        }
    }

    //EditTextのTextからPostを取得して返す
    Post getPostFromEditText() {
        Post post = new Post();

        // @formatter:off
        post.setEvaluation  ( evaluation                                                                            );
        post.setTitle       ( ((EditText) getActivity().findViewById(R.id.titleCreate))          .getText().toString());
        post.setAuthor      ( ((EditText) getActivity().findViewById(R.id.authorCreate))         .getText().toString());
        post.setCategory    ( ((EditText) getActivity().findViewById(R.id.categoryCreate))       .getText().toString());
        post.setAcquisition ( 0, ((EditText) getActivity().findViewById(R.id.acquisitionCreate1)).getText().toString());
        post.setAcquisition ( 1, ((EditText) getActivity().findViewById(R.id.acquisitionCreate2)).getText().toString());
        post.setAcquisition ( 2, ((EditText) getActivity().findViewById(R.id.acquisitionCreate3)).getText().toString());
        post.setAction      ( 0, ((EditText) getActivity().findViewById(R.id.actionCreate1))     .getText().toString());
        post.setAction      ( 1, ((EditText) getActivity().findViewById(R.id.actionCreate2))     .getText().toString());
        post.setAction      ( 2, ((EditText) getActivity().findViewById(R.id.actionCreate3))     .getText().toString());
        // @formatter:on

        return post;
    }

    //GoogleBooksAPIと通信して結果をEditTextにセット
    private void SetEditTextViaGoogleBooksAPI(String isbn, View view) {

        //UIスレッドに戻る準備
        handler = new Handler();

        //HTTP通信の準備
        OkHttpClient okHttpClient = new OkHttpClient();

        //通信の引数を設定
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn;
        Request request = new Request.Builder().url(url).build();

        //非同期処理の内容
        Callback callback = new Callback() {

            //通信失敗時の処理
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("failure API Response", e.getLocalizedMessage());
            }

            //通信成功時の処理
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    // JsonデータをJSONObjectに変換
                    JSONObject rootJson = new JSONObject(response.body().string());

                    // Jsonデータから蔵書リストデータ"items"を取得して、UIスレッドに反映
                    JSONArray items = rootJson.getJSONArray("items");
                    View view = getActivity().findViewById(R.id.titleCreate);
                    EditTextSetter editTextSetter = new EditTextSetter(items, view);

                    //UIスレッドに戻る
                    handler.post(editTextSetter);

                } catch (JSONException e) {
                    // Jsonパースの時にエラーが発生したらログに出力する
                    e.printStackTrace();
                }
            }
        };

        //非同期処理を開始
        okHttpClient.newCall(request).enqueue(callback);
    }

    //サブスレッドのJSONをUIスレッドに反映するクラス
    private class EditTextSetter implements Runnable {

        private String title;
        private String isbn10;
        private String isbn13;
        private String imageLink;
        private View view;

        //JSONをパースしてメンバに格納
        public EditTextSetter(JSONArray items, View view) {

            //viewをセット
            this.view = view;

            try {
                //itemを取得（ISBNで検索しているので一意に決まる）
                JSONObject item = items.getJSONObject(0);

                //タイトルを取得
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                title = volumeInfo.getString("title");

                //ISBN（10桁と13桁）を取得
                JSONArray industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                String identifier;
                isbn10 = "";
                isbn13 = "";
                for (int i = 0; i < industryIdentifiers.length(); ++i) {
                    identifier = industryIdentifiers.getJSONObject(i).getString("identifier");
                    if (identifier.length() == 10) isbn10 = identifier;
                    if (identifier.length() == 13) isbn13 = identifier;
                }

                //ImageLinkを取得
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                imageLink = imageLinks.getString("thumbnail");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //UIスレッドに関する処理
        @Override
        public void run() {
            Log.d("mytest", "title : " + title);
            Log.d("mytest", "isbn10 : " + isbn10);
            Log.d("mytest", "isbn13 : " + isbn13);
            Log.d("mytest", "imageLink : " + imageLink);

            //EditTextに値をセット
            ((EditText) view).setText(title);
        }
    }
}
