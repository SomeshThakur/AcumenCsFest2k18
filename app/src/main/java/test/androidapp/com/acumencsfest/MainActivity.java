package test.androidapp.com.acumencsfest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    private WebView webView;
    private DatabaseReference mDatabase;
    private String urlVal;
    ProgressBar progressBar;
    TextView progressText;
    FloatingActionButton qrBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(isNetworkAvailable()) {

            //if network is available


            //checking if user is logged in
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(MainActivity.this, GoogleSignInActivity.class));

            }
            else {

                //checking for permissions
                while (CheckPermission() == false)
                    CheckPermission();


                //connecting to web view
                webView = findViewById(R.id.webView);
                mDatabase = FirebaseDatabase.getInstance().getReference();
                progressBar = findViewById(R.id.progressBar);
                progressText = findViewById(R.id.progressText);
                qrBtn = findViewById(R.id.qrBtn);

                qrBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(MainActivity.this,QRActivity.class));
                    }
                });


                //getting the url into urlVal variable

                mDatabase.child("webLink").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        urlVal = (String) dataSnapshot.getValue();

                        //setting up the webview
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setVisibility(View.GONE);
                        qrBtn.setVisibility(View.GONE);



                        webView.loadUrl(urlVal);


                        //hiding the action bar
                        getSupportActionBar().hide();



                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        qrBtn.setVisibility(View.VISIBLE);

                    }






                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

        }
        else
        {
            Toast.makeText(MainActivity.this,"Please Connect To Internet",Toast.LENGTH_LONG).show();
        }



    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    public boolean CheckPermission() {
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ||(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            }, 99);

        }
        return  true;



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
