package samuli.androidbeacons;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DatabaseGetter extends Thread {

    DatabaseDataAvailable notifier;

    void setNotifierDataAvailable(DatabaseDataAvailable notifier) {
        this.notifier = notifier;
    }

    JSONObject dataJsonResponse = new JSONObject();
    JSONObject userJsonResponse = new JSONObject();

    int gotResponsesAmount = 0;

    RequestQueue queue;

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
                    System.out.println(jsonResponse);

                    JSONArray jsonArray = jsonResponse.getJSONArray("data");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.has("beacon_name")) {
                        dataJsonResponse = jsonResponse;
                        gotResponsesAmount++;
                    } else if (jsonObject.has("name")) {
                        userJsonResponse = jsonResponse;
                        gotResponsesAmount++;
                    }

                    if (gotResponsesAmount == 2) {
                        notifier.dataAvailable(dataJsonResponse, userJsonResponse);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        DataGetRequestData dataRequestData = new DataGetRequestData(7, responseListener);
        queue = Volley.newRequestQueue(MainActivity.getContext());
        queue.add(dataRequestData);

        DataGetRequestUsers dataRequestUsers = new DataGetRequestUsers(responseListener);
        queue.add(dataRequestUsers);

    }

    private class DataGetRequestData extends StringRequest {

        private static final String LOGIN_REQUEST_URL = "https://testiaccountservu.gear.host/UsersDataOut.php";
        private Map<String, String> params;

        public DataGetRequestData(int user_id, Response.Listener<String> listener) {
            super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
            params = new HashMap<>();
        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }

    private class DataGetRequestUsers extends StringRequest {

        private static final String LOGIN_REQUEST_URL = "https://testiaccountservu.gear.host/Users.php";
        private Map<String, String> params;

        public DataGetRequestUsers(Response.Listener<String> listener) {
            super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
            params = new HashMap<>();
        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }
}

