package samuli.androidbeacons;


import org.json.JSONObject;

public interface DatabaseDataAvailable {
    void dataAvailable(JSONObject jsonObjectData, JSONObject jsonObjectUsers);
}
