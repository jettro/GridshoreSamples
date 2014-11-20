package nl.gridshore.eswp.elasticsearch;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Factory class to obtain the elasticsearch Client reference. This factory maintains the client. Beware if
 * you keep a local reference to the client you will not be refreshed when we need to reconnect. Therefore it
 * is better to use the method client() to obtain a reference to the client.
 */
public class TransportClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(TransportClientFactory.class);
    private static final String DEFAULT_CLUSTERNAME = "elasticsearch";
    private static final List<String> DEFAULT_HOSTS = Arrays.asList("localhost:9300");

    private String clusterName = DEFAULT_CLUSTERNAME;
    private List<String> hosts = DEFAULT_HOSTS;

    private Client client;

    public TransportClientFactory() {
        this(DEFAULT_CLUSTERNAME, DEFAULT_HOSTS);
    }

    public TransportClientFactory(String clusterName) {
        this(clusterName, DEFAULT_HOSTS);
    }

    public TransportClientFactory(String clusterName, List<String> hosts) {
        this.clusterName = clusterName;
        this.hosts = hosts;
        this.refreshClient();
    }

    /**
     * Returns a reference to the current client. Better not to store this reference to make reconnect work.
     *
     * @return Client connection to the configure elasticsearch cluster
     */
    public Client client() {
        return client;
    }

    /**
     * When receiving exceptions, you can try to reconnect to elasticsearch using this refresh mechanism
     */
    public void refreshClient() {
        if (this.client != null) {
            logger.info("Refreshing the connection to elasticsearch with clustername {} and nodes {}",
                    clusterName, hosts);
            this.client.close();
            this.client = null;
        }
        this.client = connectClient();

        if (logger.isInfoEnabled()) {
            ActionFuture<ClusterHealthResponse> health = this.client.admin().cluster().health(new ClusterHealthRequest());
            ClusterHealthStatus status = health.actionGet().getStatus();
            logger.info("Cluster status is {}", status);
        }
    }

    /**
     * It is good practise to close the connection to elasticsearch when your application is closing.
     */
    public void closeClient() {
        this.client.close();
    }

    private Client connectClient() {
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();

        List<TransportAddress> addresses = hosts.stream()
                .map(ParseServerStringFunction::parse)
                .collect(toList());

        return new TransportClient(settings)
                .addTransportAddresses(addresses.toArray(new TransportAddress[addresses.size()]));

    }
}
