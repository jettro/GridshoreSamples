package nl.gridshore.eswp;

import nl.gridshore.eswp.elasticsearch.BlogItemElasticTransformer;
import nl.gridshore.eswp.elasticsearch.BulkProcessorBuilder;
import nl.gridshore.eswp.elasticsearch.IndexCreator;
import nl.gridshore.eswp.elasticsearch.TransportClientFactory;
import nl.gridshore.eswp.wordpress.BlogItemTransformer;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by jettrocoenradie on 19/11/14.
 */
public class ImportBlog {
    private final static Logger logger = LoggerFactory.getLogger(ImportBlog.class);

    private static String clusterName = "jc-play";
    private static List<String> unicastHosts = Arrays.asList("localhost:9300");

    public static void main(String[] args) throws MalformedURLException, XmlRpcException {
        if (args.length != 2) {
            System.out.println("Use with 2 arguments: username,password");
            System.exit(-1);
        }
        String username = args[0];
        String password = args[1];
        String xmlrpcUrl = "http://www.gridshore.nl/xmlrpc2.php";

        // Create the elasticsearch client
        TransportClientFactory elastic = new TransportClientFactory(clusterName, unicastHosts);

        IndexCreator.start(elastic.client(), "gridshore")
                .settings(createSettingsString())
                .addMapping("blog", createMappingsString())
                .removeOldIndices()
                .create();

        BulkProcessor bulkProcessor = BulkProcessorBuilder.build(elastic.client());

        List<Map> items = obtainItemsFromBlog(username, password, xmlrpcUrl);

        BlogItemElasticTransformer esTransformer = new BlogItemElasticTransformer("gridshore","blog");

        items.stream()
                .map(BlogItemTransformer::transform)
                .map(esTransformer::transform)
                .forEach(bulkProcessor::add);

        // close the elasticsearch client
        try {
            bulkProcessor.awaitClose(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Error when waiting for the bulk processor to finish.", e);
        }
        elastic.closeClient();
    }

    private static List<Map> obtainItemsFromBlog(String username, String password, String xmlrpcUrl) throws MalformedURLException, XmlRpcException {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(xmlrpcUrl));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);

        Object result = client.execute(config, "metaWeblog.getRecentPosts", new Object[]{1, username, password, 10});
        return items(result);
    }

    private static String createSettingsString() {
        try {
            XContentBuilder builder = jsonBuilder()
                    .startObject()
                        .field("number_of_shards", 1)
                        .field("number_of_replicas", 0)
                    .endObject();
            return builder.string();
        } catch (IOException e) {
            logger.error("Created settings object is wrong", e);
        }
        return null;
    }

    private static String createMappingsString() {
        try {
            XContentBuilder builder = jsonBuilder()
                    .startObject()
                        .startObject("properties")
                            .startObject("title")
                                .field("type", "string")
                            .endObject()
                            .startObject("author")
                                .field("type", "string")
                                .startObject("fields")
                                    .startObject("keyword")
                                        .field("type", "string")
                                        .field("analyzer", "keyword")
                                    .endObject()
                                .endObject()
                            .endObject()
                            .startObject("categories")
                                .field("type", "string")
                                .startObject("fields")
                                    .startObject("keyword")
                                        .field("type", "string")
                                        .field("analyzer", "keyword")
                                    .endObject()
                                .endObject()
                            .endObject()
                            .startObject("keywords")
                                .field("type", "string")
                                .startObject("fields")
                                    .startObject("keyword")
                                        .field("type", "string")
                                        .field("analyzer", "keyword")
                                    .endObject()
                                .endObject()
                            .endObject()
                            .startObject("content")
                                .field("type", "string")
                            .endObject()
                            .startObject("slug")
                                .field("type", "string")
                            .endObject()
                            .startObject("status")
                                .field("type", "string")
                            .endObject()
                            .startObject("status")
                                .field("type", "string")
                            .endObject()
                        .endObject()
                    .endObject();
            return builder.string();
        } catch (IOException e) {
            logger.error("Created mappings object is wrong", e);
        }
        return null;
    }

    private static List<Map> items(Object result) {
        if (result == null) {
            return new ArrayList<Map>();
        }

        Object[] items = (Object[]) result;

        List<Map> listOfMaps = new ArrayList<>();
        for (Object object : items) {
            listOfMaps.add((Map) object);
        }

        return listOfMaps;
    }

}
