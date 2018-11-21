package samuli.androidbeacons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class JSONParser {

    static int[] parseCurrentUserTime(JSONObject jsonObject) {
        return new int[] {1, 2};
    }

    static HashMap<String, Integer> parseAllUsersTime(JSONObject jsonObject) {
        HashMap deviceTimeMap = new HashMap<String, Integer>();
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectEntry = jsonArray.getJSONObject(i);
                String device = jsonObjectEntry.getString("beacon_name");
                int time = jsonObjectEntry.getInt("seconds");
                if (deviceTimeMap.containsKey(device)) {
                    int oldTime = (int) deviceTimeMap.get(device);
                    deviceTimeMap.remove(device);
                    deviceTimeMap.put(device, oldTime + time);
                } else {
                    deviceTimeMap.put(device, time);
                }

            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return deviceTimeMap;
    }

}
