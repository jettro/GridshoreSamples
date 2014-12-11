package nl.gridshore.eswp;

import nl.gridshore.eswp.elasticsearch.SnapshotService;
import nl.gridshore.eswp.elasticsearch.TransportClientFactory;

import java.util.List;

import static nl.gridshore.eswp.Constants.*;
import static nl.gridshore.eswp.elasticsearch.SnapshotRepositoryConfig.buildFileSystem;

/**
 * With this runner we create a new snapshot from the gridshore index. If the repository is not available,
 * the repository will be created.
 */
public class RepositoryRunner {
    public static void main(String[] args) {
        TransportClientFactory factory = new TransportClientFactory(CLUSTER_NAME, UNICAST_HOSTS);

        SnapshotService snapshotService =
                new SnapshotService(factory, buildFileSystem(REPOSITORY_NAME, REPOSITORY_LOCATION));
//        snapshotService.createSnapshotRepository();

        snapshotService.createSnapshot("gridshore", "logstash-*");

        List<String> snapshots = snapshotService.showSnapshots();
        snapshots.forEach(System.out::println);

//        snapshotService.deleteSnapshot("gridshore-20141126214102"Added);

//        snapshotService.restoreSnapshot("gridshore-20141126214102");

        factory.closeClient();
    }
}
