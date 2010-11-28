package nl.gridshore.samples.springmobile.web;

import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.mvc.DeviceResolvingHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jettro Coenradie
 */
public class WithOverruleDeviceResolvingHandlerInterceptor extends DeviceResolvingHandlerInterceptor {
    public static final String mobile = "mobile";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (null != request.getParameter(mobile)) {
            boolean mobileOverruled = Boolean.parseBoolean(request.getParameter(mobile));
            Device overruledDevice;
            if (mobileOverruled) {
                overruledDevice = DummyDevice.MOBILE_INSTANCE;
            } else {
                overruledDevice = DummyDevice.NOT_MOBILE_INSTANCE;
            }
            request.setAttribute(CURRENT_DEVICE_ATTRIBUTE, overruledDevice);
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }
}
