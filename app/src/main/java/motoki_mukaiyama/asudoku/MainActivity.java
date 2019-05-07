package motoki_mukaiyama.asudoku;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FireBase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Fabric
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);

        //ツールバーの設定
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar));

        //新規投稿ボタンのイベント登録
        findViewById(R.id.actionBarAddButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OkHttpClient okHttpClient = new OkHttpClient();
//                String url = "https://www.googleapis.com/books/v1/volumes?q=ほんきで学ぶAndroidアプリ開発入門";
                String isbn = "9784309226729";
                String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn;
                Request request = new Request.Builder().url(url).build();
                handler = new Handler();

                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("failure API Response", e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
//                        Log.d("Success API Response", response.body().string());

                        try {
                            // JsonデータをJSONObjectに変換
                            JSONObject rootJson = new JSONObject(response.body().string());

                            // Jsonデータから蔵書リストデータ"items"を取得して、UIスレッドに反映
                            JSONArray items = rootJson.getJSONArray("items");
                            ReflectResult reflectResult = new ReflectResult(items);

                            //UIスレッドに戻る
                            handler.post(reflectResult);

                        } catch (JSONException e) {
                            // Jsonパースの時にエラーが発生したらログに出力する
                            e.printStackTrace();
                        }
                    }
                };

                okHttpClient.newCall(request).enqueue(callback);


//                //バーコードリーダーを起動
//                new IntentIntegrator(MainActivity.this).initiateScan();

//                //新規投稿フラグメントに遷移
//                PostCreateFragment postCreateFragment = PostCreateFragment.newInstance();
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.mainFrameLayout, postCreateFragment)
//                        .addToBackStack(null)
//                        .commit();
            }
        });

        //MobileAds の初期化
        //Sample ID : ca-app-pub-3940256099942544~3347511713
        //       ID : ca-app-pub-6583713279393026~6676624532
        MobileAds.initialize(this, "ca-app-pub-6583713279393026~6676624532");
        AdView mAdView;
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //フラグメント生成
        PostIndexFragment postIndexFragment = PostIndexFragment.newInstance();

        //フラグメント追加
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mainFrameLayout, postIndexFragment)
                .commit();
    }

    //メニュー生成 //TODO create,edit画面では非表示にしたいが、ここでしかmenuを取得できない。引数で渡すのも面倒。
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memu, menu);
        return true;
    }

    //メニュー押下
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        //プライバシーポリシーが押されたら
        if (id == R.id.menuItemPrivacyPolicy) {
            // プライバシーポリシーURLを開く
            Uri uri = Uri.parse("https://asudoku-4a8ae.firebaseapp.com/");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
        }
        return true;
    }

    //バーコードリーダーの読み取り結果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String content = intentResult.getContents();
            setTitle(content); // TODO debug タイトルに表示している
        }
    }

    //サブスレッドのJSONをUIスレッドに反映するクラス
    private class ReflectResult implements Runnable {

        // 蔵書一覧タイトルデータ
        private String title;
        // 蔵書一覧概要データ
        private String summary;

        //JSONをパースしてメンバに格納
        public ReflectResult(JSONArray items) {

            try {
                // itemを取得
                JSONObject item = items.getJSONObject(0);
                // volumeInfo（蔵書情報のグループ）を取得
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                // データをリストに追加
                title = volumeInfo.getString("title");
                summary = volumeInfo.getString("description");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //UIスレッドに関する処理
        @Override
        public void run() {
            Log.v("mytest", "title : " + title);
            Log.v("mytest", "summary : " + summary);
            setTitle(title);
        }
    }
}
