package samuli.androidbeacons;

import java.util.HashMap;

public class OwnBeacons {

    static final long CONNECTION_LOST_TIME = 2000;

    static HashMap getOwnBeaconsNameHashMap() {
        HashMap<String, String> beaconNamesMap = new HashMap<>();
        beaconNamesMap.put("CD:8F:40:E4:97:BF", "Punainen");
        beaconNamesMap.put("C9:9F:6E:43:C8:CA", "Keltainen");
        beaconNamesMap.put("E5:1B:6A:DB:B4:D7", "Pinkki");

        return beaconNamesMap;
    }

    static HashMap getOwnBeaconsDistanceHashMap() {
        HashMap<String, Double> beaconDistanceMap = new HashMap<>();
        beaconDistanceMap.put("CD:8F:40:E4:97:BF", 0.2);
        beaconDistanceMap.put("C9:9F:6E:43:C8:CA", 0.2);
        beaconDistanceMap.put("E5:1B:6A:DB:B4:D7", 0.2);

        return beaconDistanceMap;
    }

}
