package motoki_mukaiyama.asudoku;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
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

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

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
        findViewById(R.id.actionBarAddButton ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新規投稿フラグメントに遷移
                PostCreateFragment postCreateFragment = PostCreateFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, postCreateFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //MobileAds の初期化
        //Sample ID : ca-app-pub-3940256099942544~3347511713
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
        if(id == R.id.menuItemPrivacyPolicy){
            // プライバシーポリシーURLを開く
            Uri uri = Uri.parse("https://asudoku-4a8ae.firebaseapp.com/");
            Intent i = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(i);
        }
        return true;
    }
}
