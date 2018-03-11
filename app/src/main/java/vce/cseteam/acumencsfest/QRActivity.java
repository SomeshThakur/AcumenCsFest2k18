package vce.cseteam.acumencsfest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRActivity extends AppCompatActivity {

    public final static int QRcodeWidth = 1000;
    private ImageView myImage;
    private TextView walletbalance;
    private DatabaseReference databaseReference;
    private String l;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UserId");
        pb = findViewById(R.id.qr_progress);

        myImage = (ImageView) findViewById(R.id.QR);
        myImage.setVisibility(View.GONE);
        walletbalance = (TextView) findViewById(R.id.balance);
        retrievewallet();

        final File imgFile = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files/QR_Acumen.jpg");

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            myImage.setImageBitmap(myBitmap);
            myImage.setVisibility(View.VISIBLE);
        } else {
            try {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        l = (String) dataSnapshot.getValue();
                        //Toast.makeText(QRActivity.this, l, Toast.LENGTH_LONG).show();
                        try {
                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                            BitMatrix bitMatrix = null;
                            bitMatrix = multiFormatWriter.encode(l, BarcodeFormat.QR_CODE, QRcodeWidth, QRcodeWidth);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                            storeImage(bitmap);
                            pb.setVisibility(View.GONE);
                            myImage.setImageBitmap(bitmap);
                            myImage.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Toast.makeText(QRActivity.this, "Error occured ! " + l, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            } catch (Exception e) {
                Toast.makeText(QRActivity.this, "Error" + e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


        }


    }

    private String retrievewallet() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Wallet");
        final String[] wallet = new String[1];
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                walletbalance.setText("Your wallet Balance : " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return wallet[0];

    }

    //this function stores the given bitmap onto local storage in phone
    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(QRActivity.this, "File not found", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(QRActivity.this, "unable to create image file", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name

        File mediaFile;
        String mImageName = "QR_Acumen.jpg";
        mediaFile = new File(mediaStorageDir.getPath() + "/" + mImageName);
        return mediaFile;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
