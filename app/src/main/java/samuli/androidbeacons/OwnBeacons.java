package samuli.androidbeacons;

import java.util.HashMap;

public class OwnBeacons {

    static final long CONNECTION_LOST_TIME = 2000;

    static HashMap getOwnBeaconsNameHashMap() {
        HashMap<String, String> beaconNamesMap = new HashMap<>();
        beaconNamesMap.put("CD:8F:40:E4:97:BF", "Paikka 1");
        beaconNamesMap.put("C9:9F:6E:43:C8:CA", "Paikka 2");
        beaconNamesMap.put("E5:1B:6A:DB:B4:D7", "Paikka 3");
        beaconNamesMap.put("EE:CF:5C:B0:B1:DA", "Paikka 4");
        beaconNamesMap.put("DE:F6:F3:71:48:FF", "Paikka 5");

        return beaconNamesMap;
    }

    static HashMap getOwnBeaconsDistanceHashMap() {
        HashMap<String, Double> beaconDistanceMap = new HashMap<>();
        beaconDistanceMap.put("CD:8F:40:E4:97:BF", 0.2);
        beaconDistanceMap.put("C9:9F:6E:43:C8:CA", 0.2);
        beaconDistanceMap.put("E5:1B:6A:DB:B4:D7", 0.2);
        beaconDistanceMap.put("DE:F5:08:86:07:D4", 0.3);
        beaconDistanceMap.put("DE:F6:F3:71:48:FF", 0.3);

        return beaconDistanceMap;
    }

}
