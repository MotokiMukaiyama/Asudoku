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
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostEditFragment extends Fragment {

    private int evaluation;

    public void PostEdit() {
    }

    public static PostEditFragment newInstance(int postId) {

        //引数を作成
        Bundle args = new Bundle();
        args.putInt(PostIndexFragment.EXTRA_ID, postId);

        //引数を設定して、フラグメントを返す
        PostEditFragment postEditFragment = new PostEditFragment();
        postEditFragment.setArguments(args);
        return postEditFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_post_edit, container, false);

        //ツールバーのボタン表示切替
        getActivity().setTitle(R.string.ActionBarTitleEdit);
        getActivity().findViewById(R.id.actionBarAddButton).setVisibility(View.GONE);
        getActivity().findViewById(R.id.actionBarSaveButton).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.actionBarRemoveButton).setVisibility(View.VISIBLE);

        //前画面からpostIdを取得
        final int postId = getArguments().getInt(PostIndexFragment.EXTRA_ID);

        //postIdの投稿を取得
        Posts posts = new Posts();
        posts.setFromDB(getActivity(), postId);

        //デフォルトのテキストを表示
        //（Postの値をEditTextにセット）
        setEditTextToDefaultText(view, posts.get(0));

        //フォーカスのイベント
        view.findViewById(R.id.titleEdit).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                //バリデーション
                validate(view, hasFocus);
            }
        });

        //保存ボタンのイベント
        getActivity().findViewById(R.id.actionBarSaveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EditTextからPostを設定
                Post post = getPostFromEditText();

                //IDを設定
                post.setId(postId);

                //作成日を設定
                String now = new SimpleDateFormat(getString(R.string.dateShowFormat), Locale.JAPAN).format(new Date());
                post.setUpdatedDate(now);

                //DBを更新する
                post.updateDB(getActivity(), false);

                //ソフトウェアキーボードを閉じる
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

        //削除ボタンのクリックイベント
        getActivity().findViewById(R.id.actionBarRemoveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //削除対象の投稿IDをセットし、削除確認フラグメントへ遷移
                int postId = getArguments().getInt(PostIndexFragment.EXTRA_ID);
                PostRemoveConfirmFragment fragment = PostRemoveConfirmFragment.newInstance(postId);
                getFragmentManager()
                        .beginTransaction()
//                        .remove(this)
                        .add(R.id.mainFrameLayout, fragment)
                        .commit();
            }
        });

        //TODO createと共通化する
        //評価のクリックイベント
        View includeView = view.findViewById(R.id.include_views_evaluation_star_edit);
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

    //投稿情報をEditTextのTextに設定
    void setEditTextToDefaultText(View view, Post post) {

        //EditTextに値を設定
        // @formatter:off
        evaluation = post.getEvaluation();
        setEvaluationImage(view, evaluation);
        ((EditText)view.findViewById(R.id.titleEdit        )). setText(post.getTitle()                          );
        ((EditText)view.findViewById(R.id.authorEdit       )). setText(post.getAuthor()                         );
        ((EditText)view.findViewById(R.id.categoryEdit     )). setText(post.getCategory()                       );
        ((EditText)view.findViewById(R.id.acquisitionEdit1 )). setText(post.getAcquisition(0, ""));
        ((EditText)view.findViewById(R.id.acquisitionEdit2 )). setText(post.getAcquisition(1, ""));
        ((EditText)view.findViewById(R.id.acquisitionEdit3 )). setText(post.getAcquisition(2, ""));
        ((EditText)view.findViewById(R.id.actionEdit1      )). setText(post.getAction     (0, ""));
        ((EditText)view.findViewById(R.id.actionEdit2      )). setText(post.getAction     (1, ""));
        ((EditText)view.findViewById(R.id.actionEdit3      )). setText(post.getAction     (2, ""));
        // @formatter:on
    }

    //EditTextのTextからPostを取得して返す
    Post getPostFromEditText() {
        Post post = new Post();

        // @formatter:off
        post.setEvaluation  ( evaluation                                                                            );
        post.setTitle       ( ((EditText) getActivity().findViewById(R.id.titleEdit))          .getText().toString());
        post.setAuthor      ( ((EditText) getActivity().findViewById(R.id.authorEdit))         .getText().toString());
        post.setCategory    ( ((EditText) getActivity().findViewById(R.id.categoryEdit))       .getText().toString());
        post.setAcquisition ( 0, ((EditText) getActivity().findViewById(R.id.acquisitionEdit1)).getText().toString());
        post.setAcquisition ( 1, ((EditText) getActivity().findViewById(R.id.acquisitionEdit2)).getText().toString());
        post.setAcquisition ( 2, ((EditText) getActivity().findViewById(R.id.acquisitionEdit3)).getText().toString());
        post.setAction      ( 0, ((EditText) getActivity().findViewById(R.id.actionEdit1))     .getText().toString());
        post.setAction      ( 1, ((EditText) getActivity().findViewById(R.id.actionEdit2))     .getText().toString());
        post.setAction      ( 2, ((EditText) getActivity().findViewById(R.id.actionEdit3))     .getText().toString());
        // @formatter:on

        return post;
    }

    //TODO 共通関数化する
    public static void setEvaluationImage(View view, int evaluation) {
        // @formatter:off
        ((ImageView) view.findViewById(R.id.evaluationStar1)).setImageResource(R.drawable.ic_empty_star);
        ((ImageView) view.findViewById(R.id.evaluationStar2)).setImageResource(R.drawable.ic_empty_star);
        ((ImageView) view.findViewById(R.id.evaluationStar3)).setImageResource(R.drawable.ic_empty_star);
        ((ImageView) view.findViewById(R.id.evaluationStar4)).setImageResource(R.drawable.ic_empty_star);
        ((ImageView) view.findViewById(R.id.evaluationStar5)).setImageResource(R.drawable.ic_empty_star);
        if(evaluation>=1) ((ImageView) view.findViewById(R.id.evaluationStar1)).setImageResource(R.drawable.ic_star);
        if(evaluation>=2) ((ImageView) view.findViewById(R.id.evaluationStar2)).setImageResource(R.drawable.ic_star);
        if(evaluation>=3) ((ImageView) view.findViewById(R.id.evaluationStar3)).setImageResource(R.drawable.ic_star);
        if(evaluation>=4) ((ImageView) view.findViewById(R.id.evaluationStar4)).setImageResource(R.drawable.ic_star);
        if(evaluation>=5) ((ImageView) view.findViewById(R.id.evaluationStar5)).setImageResource(R.drawable.ic_star);
        // @formatter:on
    }
}
