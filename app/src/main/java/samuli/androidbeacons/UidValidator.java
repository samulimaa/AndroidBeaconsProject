package samuli.androidbeacons;

import android.util.Log;

import java.util.Arrays;


/**
 * Basic validation of an Eddystone-UID frame. <p>
 *
 * @see <a href="https://github.com/google/eddystone/eddystone-uid">UID frame specification</a>
 */
public class UidValidator {

  private static final String TAG = UidValidator.class.getSimpleName();
  private static final int MIN_EXPECTED_TX_POWER = -100;
  private static final int MAX_EXPECTED_TX_POWER = 20;

  private UidValidator() {
  }

  static void validate(String deviceAddress, byte[] serviceData, Beacon beacon) {
    beacon.hasUidFrame = true;

    // Tx power should have reasonable values.
    int txPower = (int) serviceData[1];
    beacon.txPower = txPower;
    if (txPower < MIN_EXPECTED_TX_POWER || txPower > MAX_EXPECTED_TX_POWER) {
      String err = String
          .format("Expected UID Tx power between %d and %d, got %d", MIN_EXPECTED_TX_POWER,
              MAX_EXPECTED_TX_POWER, txPower);
      beacon.errTx = err;
      logDeviceError(deviceAddress, err);
    }

    // The namespace and instance bytes should not be all zeroes.
    byte[] uidBytes = Arrays.copyOfRange(serviceData, 2, 18);
    beacon.uidValue = Utils.toHexString(uidBytes);
    if (Utils.isZeroed(uidBytes)) {
      String err = "UID bytes are all 0x00";
      beacon.errUid = err;
      logDeviceError(deviceAddress, err);
    }

    // If we have a previous frame, verify the ID isn't changing.
    if (beacon.uidServiceData == null) {
      beacon.uidServiceData = serviceData.clone();
    } else {
      byte[] previousUidBytes = Arrays.copyOfRange(beacon.uidServiceData, 2, 18);
      if (!Arrays.equals(uidBytes, previousUidBytes)) {
        String err = String.format("UID should be invariant.\nLast: %s\nthis: %s",
            Utils.toHexString(previousUidBytes),
            Utils.toHexString(uidBytes));
        beacon.errUid = err;
        logDeviceError(deviceAddress, err);
        beacon.uidServiceData = serviceData.clone();
      }
    }

    // Last two bytes in frame are RFU and should be zeroed.
    byte[] rfu = Arrays.copyOfRange(serviceData, 18, 20);
    if (rfu[0] != 0x00 || rfu[1] != 0x00) {
      String err = "Expected UID RFU bytes to be 0x00, were " + Utils.toHexString(rfu);
      beacon.errRfu = err;
      logDeviceError(deviceAddress, err);
    }
  }

  private static void logDeviceError(String deviceAddress, String err) {
    Log.e(TAG, deviceAddress + ": " + err);
  }
}
