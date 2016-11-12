package com.dh.easyweather;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dh on 16-11-11.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                jumpMainActivity();
                finish();
            }
        }).start();
    }

        /*
        RelativeLayout adsParent = (RelativeLayout) this.findViewById(R.id.adsRl);
        SplashAdListener listener = new SplashAdListener() {
            @Override
            public void onAdPresent() {
                Log.i("RSplashActivity", "onAdPresent");
            }

            @Override
            public void onAdDismissed() {
                Log.i("RSplashActivity", "onAdDismissed");
                jumpWhenCanClick();
            }

            @Override
            public void onAdFailed(String s) {
                Log.i("RSplashActivity", "onAdFailed");
                jumpWhenCanClick();
            }

            @Override
            public void onAdClick() {
                Log.i("RSplashActivity", "onAdClick");
            }
        };
        String adPlaceId = "myAdPlaceId";
        new SplashAd(this, adsParent, listener, adPlaceId, true);
    }*/

    private void jumpMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
