package tv.xiaocong.sdk;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import android.util.Log;

public final class XcUtils {

    private static final String LOG_TAG = XcUtils.class.getSimpleName();

    private static String SERVER = null;

    /**
     * Get a code to identify the device.
     * 
     * @return may return null
     */
    public static final String getServer() {
        if (SERVER != null) {
            return SERVER;
        }

        Properties prop = new Properties();
        FileInputStream fis = null;
        try {
            try {
                fis = new FileInputStream("/system/build.prop");
                prop.load(fis);
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "getServer", e);
            return "";
        }

        String model = prop.getProperty("ro.product.model", "");
        String name = prop.getProperty("ro.product.name", "");
        String device = prop.getProperty("ro.product.device", "");

        SERVER = model + "#" + name + "#" + device + "#";
        Log.i(LOG_TAG, "Server: " + SERVER);
        return SERVER;
    }

    /**
     * 'Hardware' used as client identifier. Try to use eth0 MAC address. If unavailable, try to use
     * wlan0 MAC address. If failed again, return null.
     * 
     * @return a MAC address as client'd identifier
     */
    public static final String getHardware() {
        String mac = getMAC("eth0");
        if (mac != null) {
            return mac;
        }

        return getMAC("wlan0");
    }

    /**
     * Returns MAC address of the given interface name.
     * 
     * @param interfaceName
     *            eth0, wlan0
     * @return mac address or null
     */
    public static String getMAC(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return null;
                }
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++) {
                    buf.append(String.format("%02X:", mac[idx]));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }

            return null;
        } catch (SocketException e) {
            Log.e(LOG_TAG, "getMAC", e);
            return null;
        }
    }

    /** Return a string of current time. The format is 'yyyyMMddHHmmssSSS'. */
    public static String getTimestampString() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        return df.format(new Date());
    }

    public static void copyStream(InputStream input, OutputStream out) throws IOException {
        int read = 0;
        byte[] buffer = new byte[16 * 1024];
        while ((read = input.read(buffer)) > 0) {
            out.write(buffer, 0, read);
        }
    }
}
