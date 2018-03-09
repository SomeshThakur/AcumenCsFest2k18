package vce.cseteam.acumencsfest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton qrBtn, eventsBtn;
    ProgressDialog pd;
    Long usercount;
    private WebView webView;
    private String urlVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        usercount = pref.getLong("user_number", 0);


        if (isNetworkAvailable()) {

            //if network is available
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                //if user is not logged in
                startActivity(new Intent(MainActivity.this, GoogleSignInActivity.class));
                finish();
            } else if (usercount == 0) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
            } else {

                //checking for permissions
                while (!CheckPermission())
                    CheckPermission();


                //connecting to web view
                webView = findViewById(R.id.webView);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                qrBtn = findViewById(R.id.qrBtn);
                eventsBtn = findViewById(R.id.eventbtn);
                webView.getSettings().setJavaScriptEnabled(true);

                qrBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(MainActivity.this, QRActivity.class));

                    }
                });


                eventsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, EventsListActivity.class));

                    }
                });

                pd = new ProgressDialog(this);
                pd.setMessage("Personalizing Your Assistant Please Wait...");
                pd.setCancelable(false);
                pd.show();

                webView.setWebChromeClient(new WebChromeClient() {

                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        if (newProgress == 100) {
                            pd.dismiss();
                        }
                    }
                });

                //getting the url into urlVal variable

                mDatabase.child("webLink").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        urlVal = (String) dataSnapshot.getValue();

                        webView.loadUrl(urlVal);

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

        } else {
            Toast.makeText(MainActivity.this, "Please Connect To Internet", Toast.LENGTH_LONG).show();
        }


    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public boolean CheckPermission() {
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            }, 99);

        }
        return true;


    }

    @Override
    public void onBackPressed() {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to close ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }
}
