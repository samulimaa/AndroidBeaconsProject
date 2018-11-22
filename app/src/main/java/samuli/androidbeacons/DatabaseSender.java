package samuli.androidbeacons;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DatabaseSender extends Thread {

    static boolean sendingEnabled = true;

    int user_id = 7;

    DatabaseSender() {
    }

    void sendToDatabase(Beacon beacon, long time) {

        user_id = 7;

        if (sendingEnabled) {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("onResponse:" + response);
                }
            };

            //DataSendRequest dataSendRequest = new DataSendRequest(this.user_id, this.time, this.beaconName, responseListener);
            DataSendRequest dataSendRequest = new DataSendRequest(user_id, time, beacon.deviceName, getCurrentDateString(), responseListener);

            RequestQueue queue = Volley.newRequestQueue(MainActivity.getContext());
            queue.add(dataSendRequest);
            System.out.println(getCurrentDateString());
        } else {
            System.out.println("Database sending not enabled");
        }
    }

    private String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(calendar.getTime());
    }

    private class DataSendRequest extends StringRequest {

        private static final String REGISTER_REQUEST_URL = "http://testiaccountservu.gear.host/DataIn.php";
        private Map<String, String> params;

        DataSendRequest(int user_id, long time, String beaconName, String date, Response.Listener<String> listener) {
            super(Method.POST, REGISTER_REQUEST_URL, listener, null);
            System.out.println(time);
            params = new HashMap<>();
            user_id = 7;
            params.put("user_id", "" + user_id);
            params.put("seconds", "" + time);
            params.put("beacon_name", beaconName);
            params.put("date", date);
        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }
}






