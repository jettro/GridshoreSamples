package nl.gridshore.eswp.elasticsearch;

import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequestBuilder;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequestBuilder;
import org.elasticsearch.action.admin.cluster.repositories.verify.VerifyRepositoryRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequestBuilder;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.repositories.RepositoryMissingException;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to help interacting with elasticsearch to manager snapshots. Using this class you can create a repository,
 * create a snapshot, list all snapshots and restore a snapshot.
 */
public class SnapshotService {
    private static final Logger logger = LoggerFactory.getLogger(SnapshotService.class);
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private TransportClientFactory clientFactory;

    private SnapshotRepositoryConfig repoConfig;

    /**
     * The snapshot repository needs a SnapshotRepositoryConfig, in the case that the config only contains the name we
     * assume that the repository exists. If you do provide the type and the location, we will create the repository if
     * it does not exists yet.
     *
     * @param clientFactory TransportClientFactory used to connect to elasticsearch
     * @param repoConfig    SnapshotRepositoryConfig containing information about the snapshot repository to use
     */
    public SnapshotService(TransportClientFactory clientFactory, SnapshotRepositoryConfig repoConfig) {
        this.clientFactory = clientFactory;
        this.repoConfig = repoConfig;
        createSnapshotRepository();
    }

    /**
     * Create a snapshot in the configured repository using a name based on the provided snapshotPrefix combined with
     * a timestamp. The indexes to include in the snapshot are specified by the patternToSnapshot.
     * <p>
     * If the configured snapshot repository contains a type and a location we will create the snapshot repository if
     * it does not exist yet.
     *
     * @param snapshotPrefix    String containing the prefix part of the name for the snapshot.
     * @param patternToSnapshot String containing a pattern for the indices to include in the snapshot
     */
    public void createSnapshot(String snapshotPrefix, String patternToSnapshot) {
        ClusterAdminClient adminClient = this.clientFactory.client().admin().cluster();

        GetRepositoriesRequestBuilder getRepo = new GetRepositoriesRequestBuilder(adminClient, repoConfig.getName());
        try {
            getRepo.execute().actionGet();
            new VerifyRepositoryRequestBuilder(adminClient, repoConfig.getName()).execute().actionGet();
        } catch (RepositoryMissingException e) {
            logger.warn("The requested repository '{}' does not exist.", repoConfig.getName());
            if (repoConfig.getType() != null && repoConfig.getLocation() != null) {
                createSnapshotRepository();
            } else {
                throw e;
            }
        }

        CreateSnapshotRequestBuilder builder = new CreateSnapshotRequestBuilder(adminClient);
        String snapshot = snapshotPrefix + "-" + LocalDateTime.now().format(dateTimeFormatter);
        builder.setRepository(repoConfig.getName())
                .setIndices(patternToSnapshot)
                .setSnapshot(snapshot);
        builder.execute().actionGet();
        logger.info("Snapshot is created with name {} in the repository {}", snapshot, repoConfig.getName());
    }

    /**
     * Returns a list containing all the names of existing snapshots in the configured repository
     *
     * @return List of string with the snapshot names
     */
    public List<String> showSnapshots() {
        ClusterAdminClient adminClient = this.clientFactory.client().admin().cluster();
        GetSnapshotsRequestBuilder builder = new GetSnapshotsRequestBuilder(adminClient);
        builder.setRepository(repoConfig.getName());
        GetSnapshotsResponse getSnapshotsResponse = builder.execute().actionGet();
        return getSnapshotsResponse.getSnapshots().stream().map(SnapshotInfo::name).collect(Collectors.toList());
    }

    /**
     * Deletes a snapshot with the provided name.
     *
     * @param snapshot String containing the name of the snapshot to delete
     */
    public void deleteSnapshot(String snapshot) {
        ClusterAdminClient adminClient = this.clientFactory.client().admin().cluster();
        DeleteSnapshotRequestBuilder builder = new DeleteSnapshotRequestBuilder(adminClient);
        builder.setRepository(repoConfig.getName()).setSnapshot(snapshot);

        builder.execute().actionGet();
        logger.info("The snapshot {} is removed", snapshot);
    }

    /**
     * You cannot restore an open existing index, therefore we first check if an index exists that is in the snapshot.
     * If the index exists we close the index before we start the restore action.
     *
     * @param snapshot String containing the name of the snapshot in the configured repository to restore
     */
    public void restoreSnapshot(String snapshot) {
        ClusterAdminClient adminClient = this.clientFactory.client().admin().cluster();

        // Obtain the snapshot and check the indices that are in the snapshot
        GetSnapshotsRequestBuilder builder = new GetSnapshotsRequestBuilder(adminClient);
        builder.setRepository(repoConfig.getName());
        builder.setSnapshots(snapshot);
        GetSnapshotsResponse getSnapshotsResponse = builder.execute().actionGet();

        // Check if the index exists and if so, close it before we can restore it.
        ImmutableList<String> indices = getSnapshotsResponse.getSnapshots().get(0).indices();
        CloseIndexRequestBuilder closeIndexRequestBuilder =
                new CloseIndexRequestBuilder(this.clientFactory.client().admin().indices());
        closeIndexRequestBuilder.setIndices(indices.toArray(new String[indices.size()]));
        closeIndexRequestBuilder.execute().actionGet();

        // Now execute the actual restore action
        RestoreSnapshotRequestBuilder restoreBuilder = new RestoreSnapshotRequestBuilder(adminClient);
        restoreBuilder.setRepository(repoConfig.getName()).setSnapshot(snapshot);
        restoreBuilder.execute().actionGet();
        logger.info("The snapshot {} is restored", snapshot);
    }

    /**
     * If you want to assign another snapshot repository config you can use this function. If the config contains
     * information about the type and location we will create it if it does not exist.
     *
     * @param config SnapshotRepositoryConfig containing required info about the snapshot repository
     */
    public void assignSnapshotRepositoryConfiguration(SnapshotRepositoryConfig config) {
        this.repoConfig = config;
        if (repoConfig.getType() != null && repoConfig.getLocation() != null) {
            createSnapshotRepository();
        }
    }

    /**
     * Initialize the repository as provided while constructing the SnapshotService
     */
    private void createSnapshotRepository() {
        Settings settings = ImmutableSettings.builder()
                .put("location", repoConfig.getLocation())
                .build();

        PutRepositoryRequestBuilder putRepo = new PutRepositoryRequestBuilder(
                clientFactory.client().admin().cluster());
        putRepo.setName(repoConfig.getName())
                .setType(repoConfig.getType())
                .setSettings(settings)
                .execute().actionGet();

        logger.info("New snapshot repository is created '{}' at location '{}'",
                repoConfig.getName(), repoConfig.getLocation());
    }
}
