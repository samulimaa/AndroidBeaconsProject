package samuli.androidbeacons;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class DatabaseSender {

    static boolean sendingEnabled = true;

    int user_id;
    long time;
    String beaconName;

    public DatabaseSender(Beacon beacon, long time) {

        this.user_id = 1;
        this.time = time;
        this.beaconName = beacon.deviceName;
    }

    public void sendToDatabase (){

        if (sendingEnabled) {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("onResponse:" + response);
                }
            };

            DataSendRequest dataSendRequest = new DataSendRequest(this.user_id, this.time, this.beaconName, responseListener);
            RequestQueue queue = Volley.newRequestQueue(MainActivity.getContext());
            queue.add(dataSendRequest);
        } else {
            System.out.println("Database sending not enabled");
        }
    }
}
class DataSendRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = ""; //URL HERE
    private Map<String, String> params;

    /*public DataSendRequest(int user_id, long time, String beaconName, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", "name");
        params.put("age", ""+age);
        params.put("username", username);
        params.put("password", "password");
    }*/

    public DataSendRequest(int user_id, long time, String beaconName, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        System.out.println(time);
        params = new HashMap<>();
        user_id = 2;
        params.put("user_id", ""+user_id);
        params.put("seconds", "" + time);
        params.put("beacon_name", beaconName);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}




