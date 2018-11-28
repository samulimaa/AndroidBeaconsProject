package samuli.androidbeacons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    static HashMap<String, Integer> parseBeaconDates(JSONObject jsonObject, String beaconName) {
        HashMap hashMap = new HashMap<String, Integer>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectEntry = jsonArray.getJSONObject(i);
                if (jsonObjectEntry.getString("beacon_name").equals(beaconName)) {
                    String date = jsonObjectEntry.getString("date");
                    int time = jsonObjectEntry.getInt("seconds");
                    if (hashMap.containsKey(date)) {
                        int oldTime = (int) hashMap.get(date);
                        hashMap.remove(date);
                        hashMap.put(date, oldTime + time);
                    } else {
                        hashMap.put(date, time);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    static int parseBeaconsAmount(JSONObject jsonObject) {
        ArrayList<String> beaconNames = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectEntry = jsonArray.getJSONObject(i);
                String beaconName = jsonObjectEntry.getString("beacon_name");
                if (!beaconNames.contains(beaconName)) {
                    beaconNames.add(beaconName);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return beaconNames.size();
    }

    static ArrayList<String> parseAndSortBeaconNames(JSONObject jsonObject) {
        ArrayList<String> beaconNames = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectEntry = jsonArray.getJSONObject(i);
                String beaconName = jsonObjectEntry.getString("beacon_name");
                if (!beaconNames.contains(beaconName)) {
                    beaconNames.add(beaconName);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(beaconNames);
        return beaconNames;
    }

}
