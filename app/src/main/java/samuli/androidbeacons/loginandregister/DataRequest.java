package samuli.androidbeacons.loginandregister;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DataRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "https://testiaccountservu.gear.host/DataOut.php";
    private Map<String, String> params;

    public DataRequest(int user_id, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("user_id", user_id + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
