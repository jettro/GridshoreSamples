package nl.gridshore.eswp.elasticsearch;

import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequestBuilder;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequestBuilder;
import org.elasticsearch.action.admin.cluster.repositories.verify.VerifyRepositoryRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.repositories.RepositoryMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
