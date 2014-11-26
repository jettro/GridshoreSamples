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
 * Class to handle the creation and listing of snapshots.
 */
public class SnapshotCreator {
    private static final Logger logger = LoggerFactory.getLogger(SnapshotCreator.class);
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private TransportClientFactory clientFactory;

    private String repositoryName = "gridshore-repo";
    private String repositoryType = "fs";
    private String snapshotPrefix = "gridshore";
    private String indexPatternToIndex = "gridshore-*";
    private String repositoryLocation;

    public SnapshotCreator(TransportClientFactory clientFactory, String repositoryLocation) {
        this.clientFactory = clientFactory;
        this.repositoryLocation = repositoryLocation;
    }

    public void createSnapshot() {
        ClusterAdminClient adminClient = this.clientFactory.client().admin().cluster();

        GetRepositoriesRequestBuilder getRepo = new GetRepositoriesRequestBuilder(adminClient, repositoryName);
        try {
            getRepo.execute().actionGet();
            new VerifyRepositoryRequestBuilder(adminClient, repositoryName).execute().actionGet();
        } catch (RepositoryMissingException e) {
            logger.warn("The requested repository '{}' does not exist, going to create it.", repositoryName);
            createSnapshotRepository(adminClient);
        }

        CreateSnapshotRequestBuilder builder = new CreateSnapshotRequestBuilder(adminClient);
        builder.setRepository(repositoryName)
                .setIndices(indexPatternToIndex)
                .setSnapshot(snapshotPrefix + "-" + LocalDateTime.now().format(dateTimeFormatter));
        builder.execute().actionGet();
    }

    public List<String> showSnapshots() {
        ClusterAdminClient adminClient = this.clientFactory.client().admin().cluster();
        GetSnapshotsRequestBuilder builder = new GetSnapshotsRequestBuilder(adminClient);
        builder.setRepository(repositoryName);
        GetSnapshotsResponse getSnapshotsResponse = builder.execute().actionGet();
        return getSnapshotsResponse.getSnapshots().stream().map(SnapshotInfo::name).collect(Collectors.toList());
    }

    public void deleteSnapshot(String snapshot) {
        ClusterAdminClient adminClient = this.clientFactory.client().admin().cluster();
        DeleteSnapshotRequestBuilder builder = new DeleteSnapshotRequestBuilder(adminClient);
        builder.setRepository(repositoryName).setSnapshot(snapshot);

        builder.execute().actionGet();
    }

    public void restoreSnapshot(String snapshot) {
        ClusterAdminClient adminClient = this.clientFactory.client().admin().cluster();

        // Obtain the snapshot and check the indices that are in the snapshot
        GetSnapshotsRequestBuilder builder = new GetSnapshotsRequestBuilder(adminClient);
        builder.setRepository(repositoryName);
        builder.setSnapshots(snapshot);
        GetSnapshotsResponse getSnapshotsResponse = builder.execute().actionGet();

        ImmutableList<String> indices = getSnapshotsResponse.getSnapshots().get(0).indices();
        CloseIndexRequestBuilder closeIndexRequestBuilder =
                new CloseIndexRequestBuilder(this.clientFactory.client().admin().indices());
        closeIndexRequestBuilder.setIndices(indices.toArray(new String[indices.size()]));
        closeIndexRequestBuilder.execute().actionGet();

        RestoreSnapshotRequestBuilder restoreBuilder = new RestoreSnapshotRequestBuilder(adminClient);
        restoreBuilder.setRepository(repositoryName).setSnapshot(snapshot);
        restoreBuilder.execute().actionGet();
    }

    private void createSnapshotRepository(ClusterAdminClient adminClient) {
        Settings settings = ImmutableSettings.builder()
                .put("location", repositoryLocation)
                .build();

        PutRepositoryRequestBuilder putRepo = new PutRepositoryRequestBuilder(adminClient);
        putRepo.setName(repositoryName)
                .setType(repositoryType)
                .setSettings(settings)
                .execute().actionGet();

        logger.info("New snapshot repositry is created '{}' at location '{}'", repositoryName, repositoryLocation);
    }
}
