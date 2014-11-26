package nl.gridshore.eswp;

import nl.gridshore.eswp.elasticsearch.SnapshotCreator;
import nl.gridshore.eswp.elasticsearch.TransportClientFactory;

import java.util.List;

import static nl.gridshore.eswp.Constants.*;

/**
 * With this runner we create a new snapshot from the gridshore index. If the repository is not available,
 * the repository will be created.
 */
public class RepositoryRunner {
    public static void main(String[] args) {
        TransportClientFactory factory = new TransportClientFactory(CLUSTER_NAME, UNICAST_HOSTS);

        SnapshotCreator snapshotCreator = new SnapshotCreator(factory, REPOSITORY_LOCATION);

//        snapshotCreator.createSnapshot();

        List<String> snapshots = snapshotCreator.showSnapshots();
        snapshots.forEach(System.out::println);

//        snapshotCreator.deleteSnapshot("gridshore-20141126214102"Added);

//        snapshotCreator.restoreSnapshot("gridshore-20141126214102");

        factory.closeClient();
    }
}
