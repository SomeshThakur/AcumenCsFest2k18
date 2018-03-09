package vce.cseteam.acumencsfest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SponsorSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor_splash);
        getSupportActionBar().hide();
        DatabaseReference mdatabase;
        final SharedPreferences pref;


        if (!isNetworkAvailable()) {
            new AlertDialog.Builder(this).setMessage("Please Connect to internet and reopen app..").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).show();
        }

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        mdatabase = FirebaseDatabase.getInstance().getReference().child("logoVisible");

        mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("logo_visible", (Boolean) dataSnapshot.getValue());
                editor.apply();
                callNextActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timerThread.start();


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void callNextActivity() {
        startActivity(new Intent(SponsorSplashActivity.this, SplashActivity.class));
        finish();
    }
}
