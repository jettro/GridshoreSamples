package nl.gridshore.samples.springmobile.web;

import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jettro Coenradie
 */
@Controller
@RequestMapping("/device")
public class DeviceController {

    @RequestMapping(method = RequestMethod.GET)
    public String info(Device device, ModelMap model) {
        model.addAttribute("device", device);
        return "device/info";
    }
}
