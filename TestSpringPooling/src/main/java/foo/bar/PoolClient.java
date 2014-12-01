package foo.bar;

import org.springframework.aop.target.CommonsPoolTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by jettrocoenradie on 01/12/14.
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PoolClient extends Thread {

    @Autowired
    private CommonsPoolTargetSource commonsPoolTargetSource;

    public PoolClient() {
    }

    @Override
    public void run() {
        PooledObject target = null;
        try {
            target = (PooledObject) commonsPoolTargetSource.getTarget();
            System.out.println("Number of active objects: " + commonsPoolTargetSource.getActiveCount());
            System.out.println(target.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (target != null) {
                try {
                    commonsPoolTargetSource.releaseTarget(target);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
