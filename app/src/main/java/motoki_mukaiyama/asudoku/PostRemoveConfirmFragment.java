package motoki_mukaiyama.asudoku;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PostRemoveConfirmFragment extends Fragment {

    public void PostRemoveConfirmFragment() {
    }

    public static motoki_mukaiyama.asudoku.PostRemoveConfirmFragment newInstance(int id) {
        motoki_mukaiyama.asudoku.PostRemoveConfirmFragment fragment = new motoki_mukaiyama.asudoku.PostRemoveConfirmFragment();
        Bundle args = new Bundle();
        args.putInt(PostIndexFragment.EXTRA_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_remove_confirm, container, false);

        //クリックイベント
        // @formatter:off
        (view.findViewById(R.id.postRemoveConfirmYes    )).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { onClickPostRemoveConfirmYes(v);      }});
        (view.findViewById(R.id.postRemoveConfirmNo     )).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { onClickPostRemoveConfirmNo(v);       }});
        (view.findViewById(R.id.postRemoveConfirmGrayout)).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { onClickPostRemoveConfirmGrayout(v);  }});
        // @formatter:on

        return view;
    }

    //はいのクリックイベント
    private void onClickPostRemoveConfirmYes(View v) {

        //Postに削除対象のIDをセット
        Post post = new Post();
        int postId = getArguments().getInt(PostIndexFragment.EXTRA_ID);
        post.setId(postId);

        //Postを削除
        post.deleteDB(getActivity());

        //前画面に遷移して画面更新
        returnToPreFragmentWithRefresh();
    }

    //いいえのクリックイベント
    private void onClickPostRemoveConfirmNo(View v) {
        returnToPreFragment();
    }

    //グレーアウトのクリックイベント
    private void onClickPostRemoveConfirmGrayout(View v) {
        returnToPreFragment();
    }

    //このフラグメントを削除する（前画面に戻る）
    private void returnToPreFragment() {
        getFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }

    //前のフラグメントに更新する（前画面に戻る＆前画面更新）
    private void returnToPreFragmentWithRefresh() {
        PostIndexFragment fragment = PostIndexFragment.newInstance();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrameLayout, fragment)
                .commit();
    }
}
