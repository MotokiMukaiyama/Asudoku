package motoki_mukaiyama.asudoku;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static motoki_mukaiyama.asudoku.PostEditFragment.setEvaluationImage;

public class PostCreateFragment extends Fragment {

    private int evaluation = 5;

    public void PostCreate() {
    }

    public static PostCreateFragment newInstance() {
        PostCreateFragment postCreateFragment = new PostCreateFragment();
        return postCreateFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_post_create, container, false);

        getActivity().setTitle(R.string.ActionBarTitleCreate);

        //ツールバーのボタン表示切替
        getActivity().findViewById(R.id.actionBarAddButton ).setVisibility(View.GONE);
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
                post.updateDB(getActivity(),true );

                //キーボードを閉じる
                InputMethodManager manager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
}
