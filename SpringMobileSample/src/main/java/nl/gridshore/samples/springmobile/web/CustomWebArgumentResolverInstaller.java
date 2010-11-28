package nl.gridshore.samples.springmobile.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.mvc.DeviceWebArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * @author Jettro Coenradie
 */
@Component
public class CustomWebArgumentResolverInstaller {

    @Autowired
    public CustomWebArgumentResolverInstaller(AnnotationMethodHandlerAdapter controllerInvoker) {
        WebArgumentResolver[] resolvers = new WebArgumentResolver[1];
        resolvers[0] = new DeviceWebArgumentResolver();
        controllerInvoker.setCustomArgumentResolvers(resolvers);
    }
}
