package vce.cseteam.acumencsfest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventsListActivity extends AppCompatActivity {


    DatabaseReference databaseReference;
    RecyclerView rview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
        rview = (RecyclerView) findViewById(R.id.recyclerview);
        rview.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("events");
        final FirebaseRecyclerAdapter<eventbean, basicactivityviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<eventbean, basicactivityviewholder>(
                eventbean.class,
                R.layout.event_card,
                basicactivityviewholder.class,
                databaseReference) {

            LinearLayout.LayoutParams params;

            @Override
            protected void populateViewHolder(final basicactivityviewholder viewHolder, final eventbean model, int position) {

                // Hide is its empty
                if (model.getEventname().trim().isEmpty()) {
                    viewHolder.mview.setVisibility(View.INVISIBLE);
                    params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.height = 0;
                    viewHolder.mview.setLayoutParams(params);
                } else
                    viewHolder.setEventName(model.getEventname());
            }
        };
        rview.setHasFixedSize(true);
        rview.setAdapter(firebaseRecyclerAdapter);


    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public static class basicactivityviewholder extends RecyclerView.ViewHolder {

        View mview;

        public basicactivityviewholder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setEventName(String eventName) {
            TextView event = (TextView) mview.findViewById(R.id.event_name);
            event.setText(eventName);

        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

}
