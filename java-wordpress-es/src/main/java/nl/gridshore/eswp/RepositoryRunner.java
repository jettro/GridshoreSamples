package nl.gridshore.eswp;

import nl.gridshore.eswp.elasticsearch.SnapshotCreator;
import nl.gridshore.eswp.elasticsearch.TransportClientFactory;

import static nl.gridshore.eswp.Constants.*;

/**
 * Created by jettrocoenradie on 26/11/14.
 */
public class RepositoryRunner {
    public static void main(String[] args) {
        TransportClientFactory factory = new TransportClientFactory(CLUSTER_NAME, UNICAST_HOSTS);

        SnapshotCreator snapshotCreator = new SnapshotCreator(factory, REPOSITORY_LOCATION);
        snapshotCreator.createSnapshot();


        factory.closeClient();
    }
}
