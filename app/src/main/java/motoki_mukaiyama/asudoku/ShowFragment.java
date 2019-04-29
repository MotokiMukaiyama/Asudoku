package motoki_mukaiyama.asudoku;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;


public class ShowFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;

    public void ShowFragment() {
    }

    public static ShowFragment newInstance(int id) {
        ShowFragment showFragment = new ShowFragment();
        Bundle args = new Bundle();
        args.putInt(PostIndexFragment.EXTRA_ID, id);
        showFragment.setArguments(args);
        return showFragment;
    }

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // FireBase DEBUG
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "123");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent("TestShowFragment", bundle);

        View view = inflater.inflate(R.layout.fragment_show, container, false);
        int postId = getArguments().getInt(PostIndexFragment.EXTRA_ID);

        getActivity().setTitle(R.string.ActionBarTitleShow);

        //ツールバーのボタン表示切替
        getActivity().findViewById(R.id.actionBarAddButton ).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.actionBarEditButton).setVisibility(View.GONE);

        //DB open
        PostOpenHelper postOpenHelper = new PostOpenHelper(getActivity());
        SQLiteDatabase db = postOpenHelper.getWritableDatabase();

        //SELECT
        Cursor c = db.query(
                "posts", //TODO 定数化
                null,
                "_id = ?",
                new String[]{Integer.toString(postId)},
                null,
                null,
                null,
                "1");


        //TextViewに値を設定
        c.moveToFirst();
        // @formatter:off
        int e = c.getInt(c.getColumnIndex(PostContract.Posts.COL_EVALUATION));
        if(e>=1) ((ImageView) view.findViewById(R.id.evaluationStar1)).setImageResource(R.drawable.ic_star);
        if(e>=2) ((ImageView) view.findViewById(R.id.evaluationStar2)).setImageResource(R.drawable.ic_star);
        if(e>=3) ((ImageView) view.findViewById(R.id.evaluationStar3)).setImageResource(R.drawable.ic_star);
        if(e>=4) ((ImageView) view.findViewById(R.id.evaluationStar4)).setImageResource(R.drawable.ic_star);
        if(e>=5) ((ImageView) view.findViewById(R.id.evaluationStar5)).setImageResource(R.drawable.ic_star);

        ((TextView)view.findViewById(R.id.title        )).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_TITLE)));
        ((TextView)view.findViewById(R.id.author       )).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_AUTHOR)));
        ((TextView)view.findViewById(R.id.category     )).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_CATEGORY)));
        ((TextView)view.findViewById(R.id.createdDate  )).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_CREATED_DATE)));
        ((TextView)view.findViewById(R.id.updatedDate  )).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_UPDATED_DATE)));
        ((TextView)view.findViewById(R.id.acquisition1 )).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_ACQUISITION1)));
        ((TextView)view.findViewById(R.id.acquisition2 )).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_ACQUISITION2)));
        ((TextView)view.findViewById(R.id.acquisition3 )).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_ACQUISITION3)));
        ((TextView)view.findViewById(R.id.action1      )).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_ACTION1)));
        ((TextView)view.findViewById(R.id.action2      )).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_ACTION2)));
        ((TextView)view.findViewById(R.id.action3      )).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_ACTION3)));
        ((TextView)view.findViewById(R.id.thoughtCreate)).setText(c.getString(c.getColumnIndex(PostContract.Posts.COL_THOUGHT)));
        // @formatter:on

        //TextViewが空文字なら非表示に設定する
        setVisibilityGone((TextView)view.findViewById(R.id.acquisition1 ));
        setVisibilityGone((TextView)view.findViewById(R.id.acquisition2 ));
        setVisibilityGone((TextView)view.findViewById(R.id.acquisition3 ));
        setVisibilityGone((TextView)view.findViewById(R.id.action1 ));
        setVisibilityGone((TextView)view.findViewById(R.id.action2));
        setVisibilityGone((TextView)view.findViewById(R.id.action3 ));
        setVisibilityGone((TextView)view.findViewById(R.id.thoughtCreate));

        //DB close
        c.close();
        db.close();

        return view;
    }

    //TextViewが空文字なら非表示に設定する
    private void setVisibilityGone(TextView view){
        if(view.getText().toString().isEmpty()) view.setVisibility(View.GONE);
    }
}
