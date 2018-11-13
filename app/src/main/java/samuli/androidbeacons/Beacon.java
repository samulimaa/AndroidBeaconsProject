package samuli.androidbeacons;

import java.util.HashMap;

class Beacon {

    final String deviceAddress;

    String deviceName = null;
    String nullServiceData;
    String invalidFrameType;
    String uidValue;
    String errTx;
    String errUid;
    String errRfu;

    double deviceMaxDistance = 0.3;

    int rssi;
    int txPower;

    byte[] uidServiceData;

    boolean hasUidFrame;
    boolean unknownDevice;

    Beacon(String deviceAddress, int rssi) {
        this.deviceAddress = deviceAddress;
        this.rssi = rssi;

        loadBeaconNames();
        //loadBeaconDistances(); uncomment to use beaconDistanceMap for manual distance selection to beacons
    }

    void loadBeaconNames() {
        HashMap<String, String> beaconNamesMap = OwnBeacons.getOwnBeaconsNameHashMap();
        if (beaconNamesMap.containsKey(deviceAddress)) {
            deviceName = beaconNamesMap.get(deviceAddress);
            unknownDevice = false;
        } else {
            deviceName = deviceAddress;
            unknownDevice = true;
        }
    }

    void loadBeaconDistances() {
        HashMap<String, Double> beaconDistanceMap = OwnBeacons.getOwnBeaconsDistanceHashMap();
        if (beaconDistanceMap. containsKey(deviceAddress)) {
            deviceMaxDistance = beaconDistanceMap.get(deviceAddress);
        }
    }
}











