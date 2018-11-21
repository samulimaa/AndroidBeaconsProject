package samuli.androidbeacons;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samuli on 21.11.2018.
 */

public class DatabaseGetter extends Thread {

    DatabaseDataAvailable notifier;

    void setNotifierDataAvailable(DatabaseDataAvailable notifier) {
        this.notifier = notifier;
    }

    @Override
    public void run() {
        getFromDatabase();
    }

    void getFromDatabase() {

        System.out.println("getfromdb");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        System.out.println("success");
                        notifier.dataAvailable(jsonResponse);
                    } else {
                        System.out.println("Request not success");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        DataGetRequest dataRequest = new DataGetRequest(7, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.getContext());
        queue.add(dataRequest);
    }

    private class DataGetRequest extends StringRequest {

        private static final String LOGIN_REQUEST_URL = "https://testiaccountservu.gear.host/DataOutTest.php";
        private Map<String, String> params;

        public DataGetRequest(int user_id, Response.Listener<String> listener) {
            super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
            params = new HashMap<>();
            params.put("user_id", user_id + "");
        }
        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }
}

