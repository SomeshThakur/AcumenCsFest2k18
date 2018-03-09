package vce.cseteam.acumencsfest;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Manu on 04-03-2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String Token = FirebaseInstanceId.getInstance().getToken();
        Log.d("mylog", "MyToken is :" + Token);
    }
}
