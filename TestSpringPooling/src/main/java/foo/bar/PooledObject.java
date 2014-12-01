package foo.bar;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by jettrocoenradie on 01/12/14.
 */
@Component("pooledObjectTarget")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PooledObject {
    private String message = "Default message: " + new Date().getTime();

    public PooledObject() {
        System.out.println("New PooledObject created");
    }

    public String getMessage() {
        try {
            TimeUnit.SECONDS.sleep(2l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
