package nl.gridshore.eswp;

import nl.gridshore.eswp.elasticsearch.BlogItemElasticTransformer;
import nl.gridshore.eswp.elasticsearch.BulkProcessorBuilder;
import nl.gridshore.eswp.elasticsearch.IndexCreator;
import nl.gridshore.eswp.elasticsearch.TransportClientFactory;
import nl.gridshore.eswp.wordpress.BlogItemTransformer;
import nl.gridshore.eswp.wordpress.WordpressInteraction;
import org.apache.xmlrpc.XmlRpcException;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static nl.gridshore.eswp.Constants.*;
import static nl.gridshore.eswp.elasticsearch.SettingsMappingsBuilder.createMappingsString;
import static nl.gridshore.eswp.elasticsearch.SettingsMappingsBuilder.createSettingsString;

/**
 * This is the class to run if you want to import your blog. There are two arguments that you need to provide:
 * <ol>
 * <li>username</li>
 * <li>password</li>
 * </ol>
 */
public class ImportBlog {
    private final static Logger logger = LoggerFactory.getLogger(ImportBlog.class);

    // Wordpress blog configuration
    private final static int NUM_ITEMS_TO_FETCH = 300;
    private final static int ITEMS_TO_START_FROM = 1;
    private final static String XMLRPC_URL = "http://www.gridshore.nl/xmlrpc2.php";

    private TransportClientFactory elastic;
    private WordpressInteraction wordpressInteraction;

    public static void main(String[] args) throws MalformedURLException, XmlRpcException {
        if (args.length != 2) {
            System.out.println("Use with 2 arguments: username,password");
            System.exit(-1);
        }
        String username = args[0];
        String password = args[1];

        // Do the actual importing
        ImportBlog importBlog = new ImportBlog();
        importBlog.initialize(username, password);
        importBlog.doImportBlog();
        importBlog.tearDown();
    }

    /**
     * Initializes the contact with the Wordpress blog and elasticsearch
     *
     * @param username String containing the username for the blog
     * @param password String containing the password for the blog
     */
    public void initialize(String username, String password) {
        this.wordpressInteraction = new WordpressInteraction(username, password, XMLRPC_URL);
        this.elastic = new TransportClientFactory(CLUSTER_NAME, UNICAST_HOSTS);

        IndexCreator.start(elastic.client(), INDEX_NAME)
                .settings(createSettingsString())
                .addMapping(INDEX_TYPE, createMappingsString())
                .removeOldIndices()
                .create();
    }

    /**
     * Method to call to start the actual reading and importing of blog items.
     */
    public void doImportBlog() {
        BulkProcessor bulkProcessor = BulkProcessorBuilder.build(elastic.client());
        BlogItemElasticTransformer esTransformer = new BlogItemElasticTransformer(INDEX_NAME, INDEX_TYPE);

        List<Map> items = wordpressInteraction.obtainItemsFromBlog(NUM_ITEMS_TO_FETCH, ITEMS_TO_START_FROM);

        items.stream()
                .map(BlogItemTransformer::transform)
                .map(esTransformer::transform)
                .forEach(bulkProcessor::add);

        try {
            bulkProcessor.awaitClose(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Error when waiting for the bulk processor to finish.", e);
        }
    }

    /**
     * Closing connection that we do not need anymore.
     */
    public void tearDown() {
        elastic.closeClient();
    }
}
