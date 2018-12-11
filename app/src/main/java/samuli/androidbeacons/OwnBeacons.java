package samuli.androidbeacons;

import java.util.HashMap;

public class OwnBeacons {

    static final long CONNECTION_LOST_TIME = 2000;

    static HashMap getOwnBeaconsNameHashMap() {
        HashMap<String, String> beaconNamesMap = new HashMap<>();
        beaconNamesMap.put("CD:8F:40:E4:97:BF", "Penkkipaikka");
        beaconNamesMap.put("C9:9F:6E:43:C8:CA", "Kyykkypaikka");
        beaconNamesMap.put("E5:1B:6A:DB:B4:D7", "Maastavetopaikka");
        beaconNamesMap.put("EE:CF:5C:B0:B1:DA", "Talja");
        beaconNamesMap.put("DE:F6:F3:71:48:FF", "Pukuhuone");

        return beaconNamesMap;
    }

    static HashMap getOwnBeaconsDistanceHashMap() {
        HashMap<String, Double> beaconDistanceMap = new HashMap<>();
        beaconDistanceMap.put("CD:8F:40:E4:97:BF", 0.1);
        beaconDistanceMap.put("C9:9F:6E:43:C8:CA", 0.1);
        beaconDistanceMap.put("E5:1B:6A:DB:B4:D7", 0.1);
        beaconDistanceMap.put("EE:CF:5C:B0:B1:DA", 0.1);
        beaconDistanceMap.put("DE:F6:F3:71:48:FF", 0.1);

        return beaconDistanceMap;
    }

}
