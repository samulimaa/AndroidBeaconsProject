package samuli.androidbeacons;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TimeMeasure {

    private HashMap<Beacon, TimeMeasuringThread> beaconTimeMeasureMap = new HashMap<>();

    private Beacon currentBeacon;

    private boolean measureStopped;

    private boolean measureDataValid;
    private long measuredTime;


    public void startMeasuringTime(Beacon b) {
        TimeMeasuringThread timeMeasurementThread = new TimeMeasuringThread(b);
        timeMeasurementThread.start();
        measureStopped = false;
        measureDataValid = false;
        beaconTimeMeasureMap.put(b, timeMeasurementThread);
    }

    public void stopMeasuringTime(TimeMeasuringThread timeMeasuringThread) {
        Beacon measuredBeacon = timeMeasuringThread.getBeacon();
        beaconTimeMeasureMap.remove(measuredBeacon);
        measuredTime = timeMeasuringThread.getMeasuredTime() - OwnBeacons.CONNECTION_LOST_TIME;
        timeMeasuringThread.stopThread();
        measureStopped = true;
        if (getMeasuredTimeInSeconds() > 1) {
            measureDataValid = true;
            DatabaseSender databaseSender = new DatabaseSender(measuredBeacon, getMeasuredTimeInSeconds());
            databaseSender.sendToDatabase();
        }
    }

    public void setBeaconLastSeen(Beacon b) {
        TimeMeasuringThread thread = beaconTimeMeasureMap.get(b);
        thread.setLastSeen();
    }

    public void checkIfStop() {
        if (beaconTimeMeasureMap.containsKey(currentBeacon)) {
            //debugToConsole("check if stop");
            TimeMeasuringThread thread = beaconTimeMeasureMap.get(currentBeacon);
            if (System.currentTimeMillis() - thread.getLastSeen() > OwnBeacons.CONNECTION_LOST_TIME) { //connection lost over 2s
                stopMeasuringTime(thread);
            }
        }
    }

    public void setCurrentBeacon(Beacon b) {
        currentBeacon = b;
        measureStopped = false;
    }

    public String getBeaconName() {
        return currentBeacon.deviceName;
    }

    public boolean isMeasureStopped() {
        return measureStopped;
    }

    public boolean isMeasureDataValid() {
        return measureDataValid;
    }

    public long getMeasuredTimeInSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(measuredTime);
    }

    public long getCurrentTimeInSeconds() {
        TimeMeasuringThread thread = beaconTimeMeasureMap.get(currentBeacon);
        long currentTime = thread.getCurrentTime();
        return TimeUnit.MILLISECONDS.toSeconds(currentTime);
    }

    public boolean beaconTimeMeasureMapContains(Beacon b) {
        return beaconTimeMeasureMap.containsKey(b);
    }
}


class TimeMeasuringThread extends Thread {

    private volatile boolean running;
    private long startTime;
    private long currentTime;
    private long measuredTime;
    private long lastSeen;
    private Beacon beacon;

    TimeMeasuringThread(Beacon b) {
        this.beacon = b;
        running = true;
    }

    @Override
    public void run() {
        System.out.println("Time measuring thread started");
        startTime = System.currentTimeMillis();
        while (running) {
            currentTime = System.currentTimeMillis();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void stopThread() {
        running = false;
    }

    long getMeasuredTime() {
        measuredTime = currentTime - startTime;
        if (measuredTime < 2000) {
            return 0;
        } else {
            return measuredTime;
        }
    }

    long getCurrentTime() {
        return currentTime - startTime;
    }

    Beacon getBeacon() {
        return beacon;
    }

    void setLastSeen() {
        lastSeen = System.currentTimeMillis();
    }

    long getLastSeen() {
        return lastSeen;
    }
}

