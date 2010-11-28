package nl.gridshore.samples.springmobile.web;

import org.springframework.mobile.device.Device;

/**
 * @author Jettro Coenradie
 */
public class DummyDevice implements Device {

    public static final DummyDevice MOBILE_INSTANCE = new DummyDevice(true);

    public static final DummyDevice NOT_MOBILE_INSTANCE = new DummyDevice(false);

    public boolean isMobile() {
        return mobile;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[DummyDevice ");
        builder.append("mobile").append("=").append(isMobile());
        builder.append("]");
        return builder.toString();
    }

    private final boolean mobile;

    /**
     * Creates a GenericDevice.
     */
    private DummyDevice(boolean mobile) {
        this.mobile = mobile;
    }
}
