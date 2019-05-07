package motoki_mukaiyama.asudoku;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class PostIndexFragment extends ListFragment {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_ISBN = "isbn";

    public PostIndexFragment() {
    }

    public static PostIndexFragment newInstance() {
        return new PostIndexFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(R.string.ActionBarTitlePosts);

        //TODO 関数化する fragment違うとfindViewById呼び出せない？
        //ツールバーのボタン表示切替
        getActivity().findViewById(R.id.actionBarAddButton ).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.actionBarSaveButton).setVisibility(View.GONE);
        getActivity().findViewById(R.id.actionBarRemoveButton).setVisibility(View.GONE);

        //投稿を取得
        Posts posts = new Posts();
        posts.setFromDB(getActivity());

        //アダプターをセット
        PostAdapter adapter = new PostAdapter(getActivity(), 0, posts.get());

        // ListViewに表示
        setListAdapter(adapter);

        //クリックイベント
        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //クリックされた箇所によってイベント分岐
                switch (view.getId()) {
                    //リストアイテム全体
                    case R.id.postsListViewWhole:
                        onListItemClick(view);
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected switch value on onItemClick");
                }
            }
        });

        //Listが空の場合の処理 TODO 空でも表示されない
        {
            // View を作って
            TextView textView = new TextView(getActivity());
            textView.setText("empty");

            // 子ビューとして追加しておいて
            ((ViewGroup) getListView().getParent()).addView(textView);

            // EmptyView に登録する
            listView.setEmptyView(textView);
        }
    }

    //リストアイテムのクリックイベント
    private void onListItemClick(View view) {

        //投稿idを取得
        View idView = view.findViewById(R.id.id); //クリックされたviewのidに一致するviewを取得
        int postId = Integer.valueOf(((TextView) idView).getText().toString());

        //フラグメント入れ替え
        PostEditFragment postEditFragment = PostEditFragment.newInstance(postId);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrameLayout, postEditFragment)
                .addToBackStack(null)
                .commit();
    }

    //TODO メモリの解放？
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        if(imageView != null){
//            // src に画像を設定した場合はこっち
//            imageView.setImageDrawable(null);
//            // background に画像を設定した場合はこっち
//            imageView.setBackgroundDrawable(null);
//        }
//    }

    //カスタムビューのアダプタ
    public class PostAdapter extends ArrayAdapter<Post> {

        private LayoutInflater layoutInflater;

        private PostAdapter(Context c, int id, List<Post> users) {
            super(c, id, users);
            this.layoutInflater = (LayoutInflater) c.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE
            );
        }

        @NonNull
        @Override
        public View getView(final int pos, View convertView, @NonNull final ViewGroup parent) {

            ViewHolder viewHolder = new ViewHolder();

            //viewが未作成の場合は作成する
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.viewgroup_post_items, parent, false);

                // @formatter:off
                viewHolder.id               = convertView.findViewById(R.id.id);
                viewHolder.title            = convertView.findViewById(R.id.title);
                viewHolder.category         = convertView.findViewById(R.id.category);
                viewHolder.createdDate      = convertView.findViewById(R.id.createdDate);
                viewHolder.evaluationStar1  = convertView.findViewById(R.id.evaluationStar1);
                viewHolder.evaluationStar2  = convertView.findViewById(R.id.evaluationStar2);
                viewHolder.evaluationStar3  = convertView.findViewById(R.id.evaluationStar3);
                viewHolder.evaluationStar4  = convertView.findViewById(R.id.evaluationStar4);
                viewHolder.evaluationStar5  = convertView.findViewById(R.id.evaluationStar5);
                viewHolder.acquisition1     = convertView.findViewById(R.id.acquisition1);
                viewHolder.acquisition2     = convertView.findViewById(R.id.acquisition2);
                viewHolder.acquisition3     = convertView.findViewById(R.id.acquisition3);
                viewHolder.action1          = convertView.findViewById(R.id.action1);
                viewHolder.action2          = convertView.findViewById(R.id.action2);
                viewHolder.action3          = convertView.findViewById(R.id.action3);
                viewHolder.acquisitionText1 = viewHolder.acquisition1.findViewById(R.id.text);
                viewHolder.acquisitionText2 = viewHolder.acquisition2.findViewById(R.id.text);
                viewHolder.acquisitionText3 = viewHolder.acquisition3.findViewById(R.id.text);
                viewHolder.actionText1      = viewHolder.action1.findViewById(R.id.text);
                viewHolder.actionText2      = viewHolder.action2.findViewById(R.id.text);
                viewHolder.actionText3      = viewHolder.action3.findViewById(R.id.text);
                // @formatter:on
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Post post = getItem(pos);

            // @formatter:off
            int e = post.getEvaluation();
            if(e>=1) viewHolder.evaluationStar1.setImageResource(R.drawable.ic_star);
            if(e>=2) viewHolder.evaluationStar2.setImageResource(R.drawable.ic_star);
            if(e>=3) viewHolder.evaluationStar3.setImageResource(R.drawable.ic_star);
            if(e>=4) viewHolder.evaluationStar4.setImageResource(R.drawable.ic_star);
            if(e>=5) viewHolder.evaluationStar5.setImageResource(R.drawable.ic_star);
//            (viewHolder.image       ).setImageBitmap(post.getImage()        );
            (viewHolder.id              ).setText(Integer.toString(post.getId()));
            (viewHolder.title           ).setText(post.getTitle()               );
            (viewHolder.category        ).setText(post.getCategory()            );
            (viewHolder.createdDate     ).setText(post.getCreatedDate()         );
            (viewHolder.acquisitionText1).setText(post.getAcquisition(0, ""));
            (viewHolder.acquisitionText2).setText(post.getAcquisition(1, ""));
            (viewHolder.acquisitionText3).setText(post.getAcquisition(2, ""));
            (viewHolder.actionText1     ).setText(post.getAction     (0, ""));
            (viewHolder.actionText2     ).setText(post.getAction     (1, ""));
            (viewHolder.actionText3     ).setText(post.getAction     (2, ""));

            // @formatter:on

            // 空文字ならViewを表示しない
            // @formatter:off
            if (post.getCategory().isEmpty())     viewHolder.category    .setVisibility(View.GONE);
            if (post.isEmptyAcquisition(0)) viewHolder.acquisition1.setVisibility(View.GONE);
            if (post.isEmptyAcquisition(1)) viewHolder.acquisition2.setVisibility(View.GONE);
            if (post.isEmptyAcquisition(2)) viewHolder.acquisition3.setVisibility(View.GONE);
            if (post.isEmptyAction     (0)) viewHolder.action1     .setVisibility(View.GONE);
            if (post.isEmptyAction     (1)) viewHolder.action2     .setVisibility(View.GONE);
            if (post.isEmptyAction     (2)) viewHolder.action3     .setVisibility(View.GONE);

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView image;
        ImageView evaluationStar1;
        ImageView evaluationStar2;
        ImageView evaluationStar3;
        ImageView evaluationStar4;
        ImageView evaluationStar5;
        TextView id;
        TextView title;
        TextView category;
        TextView createdDate;
        ViewGroup acquisition1;
        ViewGroup acquisition2;
        ViewGroup acquisition3;
        ViewGroup action1;
        ViewGroup action2;
        ViewGroup action3;
        TextView acquisitionText1;
        TextView acquisitionText2;
        TextView acquisitionText3;
        TextView actionText1;
        TextView actionText2;
        TextView actionText3;
    }

    //    正方形のImageView TODO 初期化のときにコンストラクタがないってエラーが出る
//    public class SquareImageView extends AppCompatImageView {
//        public SquareImageView(Context context) {
//            super(context);
//        }
//        public SquareImageView(Context context, AttributeSet attrs) {
//            super(context, attrs);
//        }
//        public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
//            super(context, attrs, defStyleAttr);
//        }
//
//        @Override
//        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//            int width = getMeasuredWidth();
//            setMeasuredDimension(width, width);
//        }
//    }

}