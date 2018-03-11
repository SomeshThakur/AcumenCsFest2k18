package vce.cseteam.acumencsfest;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {


    public final static int QRcodeWidth = 1000;
    Long usercount;
    private EditText collegeReg, year;
    private EditText deptReg;
    private EditText phNumReg;
    private EditText nameReg;
    private FirebaseDatabase mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //linking xm eements to java
        collegeReg = (EditText) findViewById(R.id.collegeReg);
        deptReg = (EditText) findViewById(R.id.deptReg);
        phNumReg = (EditText) findViewById(R.id.phNumReg);
        year = (EditText) findViewById(R.id.year);

        nameReg = (EditText) findViewById(R.id.nameReg);
        Button registerBtn = (Button) findViewById(R.id.registerBtn);

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Registering...");

        //connecting to firebase
        mDatabase = FirebaseDatabase.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name, college, email, dept, yr, phNum;

                name = nameReg.getText().toString();
                college = collegeReg.getText().toString();
                phNum = phNumReg.getText().toString();
                dept = deptReg.getText().toString();
                yr = year.getText().toString();

                if (name.trim().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Enter your name", Toast.LENGTH_LONG).show();
                } else if (college.trim().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Enter your College Name", Toast.LENGTH_LONG).show();
                } else if (dept.trim().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Enter your Department", Toast.LENGTH_LONG).show();
                } else if (phNum.trim().isEmpty() || phNum.length() != 10 || checkNumber(phNum)) {
                    Toast.makeText(RegisterActivity.this, "Enter a valid Phone number", Toast.LENGTH_LONG).show();
                } else if (yr.trim().isEmpty() || !(Integer.parseInt(yr) > 0 && Integer.parseInt(yr) < 5)) {
                    Toast.makeText(RegisterActivity.this, "Enter a valid current studying Year", Toast.LENGTH_LONG).show();
                } else {
                    pd.show();
                    final DatabaseReference dref = mDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("usercount");
                    final DatabaseReference userref = FirebaseDatabase.getInstance().getReference();

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            usercount = (Long) dataSnapshot.getValue();
                            mDatabase.setValue(usercount + 1);
                            userref.child("userNo").child(usercount + 1 + "").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                            dref.child("UserId").setValue(usercount + 1 + "");
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putLong("user_number", usercount + 1);
                            editor.apply();
                            pd.dismiss();
                            callMain();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    dref.child("Name").setValue(name);
                    dref.child("College").setValue(college);
                    dref.child("Phone_Number").setValue(phNum);
                    dref.child("Year").setValue(yr);
                    dref.child("Department").setValue(dept);
                    dref.child("UserDepCount").setValue(0);
                    dref.child("UserEventCount").setValue(0);
                    dref.child("UserRefCount").setValue(0);
                    dref.child("Wallet").setValue(0);
                    dref.child("events").push().child("eventname").setValue("  ");

                }
            }
        });
    }

    private boolean checkNumber(String phNum) {
        return !(phNum.startsWith("6") || phNum.startsWith("7") || phNum.startsWith("8") || phNum.startsWith("9"));
    }

    private void callMain() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
}
