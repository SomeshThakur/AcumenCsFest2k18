package test.androidapp.com.acumencsfest;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {



    //private EditText emailReg;
    private EditText collegeReg;
    private EditText deptReg;
    private EditText phNumReg;
    private EditText nameReg;
    private Button registerBtn;



    private FirebaseDatabase mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //linking xm eements to java
        collegeReg = (EditText)findViewById(R.id.collegeReg);
        deptReg = (EditText)findViewById(R.id.deptReg);
        phNumReg = (EditText)findViewById(R.id.phNumReg);
        nameReg = (EditText)findViewById(R.id.nameReg);
        registerBtn = (Button)findViewById(R.id.registerBtn);


        //connecting to firebase
        mDatabase = FirebaseDatabase.getInstance();


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name,college,email,dept,phNum;

                name = nameReg.getText().toString();
                college = collegeReg.getText().toString();
                phNum = phNumReg.getText().toString();
                dept = deptReg.getText().toString();


                if(name.equals(""))
                {
                    Toast.makeText(RegisterActivity.this,"Enter your name",Toast.LENGTH_LONG).show();
                }
                else if(college.equals(""))
                {
                    Toast.makeText(RegisterActivity.this,"Enter your College Name",Toast.LENGTH_LONG).show();
                }
                else if(dept.equals(""))
                {
                    Toast.makeText(RegisterActivity.this,"Enter your Department",Toast.LENGTH_LONG).show();
                }

                else if(phNum.equals("")  ||  phNum.matches("^[0-9]{11}$"))
                {
                    Toast.makeText(RegisterActivity.this,"Enter a valid Phone number",Toast.LENGTH_LONG).show();
                }

                else {
                    DatabaseReference dref = mDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                    final DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference().child("usercount");
                    final DatabaseReference userref=FirebaseDatabase.getInstance().getReference();

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            Long usercount = (Long) dataSnapshot.getValue();
                            mDatabase.setValue(usercount+1);

                            userref.child("userNo").child(usercount+1+"").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());




                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();

                            editor.putLong("user_number",usercount+1);
                            editor.commit();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {


                        }
                    });


                    dref.child("Name").setValue(name);
                    dref.child("College").setValue(college);
                    dref.child("Phone_Number").setValue(phNum);
                    dref.child("Department").setValue(dept);
                    dref.child("UserMsgCount").setValue(0);
                    dref = dref.child("events");

                    for (int i = 1; i <= 21; i++) {
                        dref.child(i + "").setValue(0);
                    }

                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }
            }
        });
    }
}
