package samuli.androidbeacons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class JSONParser {

    static HashMap<String, Integer> parseCurrentUserTime(JSONObject jsonObject, int user_id) {
        HashMap beaconTimeMap = new HashMap<String, Integer>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectEntry = jsonArray.getJSONObject(i);
                if(user_id == jsonObjectEntry.getInt("user_id")) {
                    String beacon_name = jsonObjectEntry.getString("beacon_name");
                    int time = jsonObjectEntry.getInt("seconds");
                    if (beaconTimeMap.containsKey(beacon_name)) {
                        int oldTime = (int) beaconTimeMap.get(beacon_name);
                        beaconTimeMap.remove(beacon_name);
                        beaconTimeMap.put(beacon_name, oldTime + time);
                    } else {
                        beaconTimeMap.put(beacon_name, time);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beaconTimeMap;
    }

    static HashMap<String, Integer> parseAllUsersTime(JSONObject jsonObject) {
        HashMap beaconTimeMap = new HashMap<String, Integer>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectEntry = jsonArray.getJSONObject(i);
                String beacon_name = jsonObjectEntry.getString("beacon_name");
                int time = jsonObjectEntry.getInt("seconds");
                if (beaconTimeMap.containsKey(beacon_name)) {
                    int oldTime = (int) beaconTimeMap.get(beacon_name);
                    beaconTimeMap.remove(beacon_name);
                    beaconTimeMap.put(beacon_name, oldTime + time);
                } else {
                    beaconTimeMap.put(beacon_name, time);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return beaconTimeMap;
    }

}
