package vce.cseteam.acumencsfest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    ImageView acumenLogo;
    TextView acumenTxt;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        acumenLogo = findViewById(R.id.acumenlogo);
        acumenTxt = findViewById(R.id.acumenTxt);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        Boolean flag = pref.getBoolean("logo_visible", false);

        if (flag) {
            acumenTxt.setVisibility(View.GONE);
            acumenLogo.setVisibility(View.VISIBLE);

        }

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);

                    startActivity(i);

                    finish();
                }
            }
        };
        timerThread.start();
    }
}
