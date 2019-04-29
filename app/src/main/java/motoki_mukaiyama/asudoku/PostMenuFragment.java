package motoki_mukaiyama.asudoku;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PostMenuFragment extends Fragment {

    public void PostMenuFragment() {
    }

    public static PostMenuFragment newInstance(int postId) {
        PostMenuFragment postMenuFragment = new PostMenuFragment();
        Bundle args = new Bundle();
        args.putInt(PostIndexFragment.EXTRA_ID, postId);
        postMenuFragment.setArguments(args);
        return postMenuFragment;
    }

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_menu, container, false);

        //クリックイベント
        // @formatter:off
        (view.findViewById(R.id.postMenuItemEdit  )).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { onClickPostMenuItemEdit(v);   }});
        (view.findViewById(R.id.postMenuItemRemove)).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { onClickPostMenuItemRemove(v); }});
        (view.findViewById(R.id.postMenuGrayout   )).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { onClickPostMenuGrayout(v);    }});
        // @formatter:on

        return view;
    }

    //編集のクリックイベント
    private void onClickPostMenuItemEdit(View v){
        int postId = getArguments().getInt(PostIndexFragment.EXTRA_ID);
        PostEditFragment fragment = PostEditFragment.newInstance(postId);
        getFragmentManager().popBackStack(); //編集フラグメントで戻るを押したらメニューフラグメントを飛ばして一覧フラグメントに戻るようにする
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    //削除のクリックイベント
    private void onClickPostMenuItemRemove(View v){
        int postId = getArguments().getInt(PostIndexFragment.EXTRA_ID);
        PostRemoveConfirmFragment fragment = PostRemoveConfirmFragment.newInstance(postId);
        getFragmentManager()
                .beginTransaction()
                .remove(this)
                .add(R.id.mainFrameLayout, fragment)
                .commit();
    }

    //グレーアウトのクリックイベント
    private void onClickPostMenuGrayout(View v){
        getFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }

}
