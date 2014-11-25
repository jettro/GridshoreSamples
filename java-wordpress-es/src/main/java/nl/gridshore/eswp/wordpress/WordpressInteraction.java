package nl.gridshore.eswp.wordpress;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains methods to interact with a Wordpress blog.
 */
public class WordpressInteraction {
    private static final Logger logger = LoggerFactory.getLogger(WordpressInteraction.class);

    private String username;
    private String password;
    private XmlRpcClientConfigImpl config;

    public WordpressInteraction(String username, String password, String xmlRpcUrl) {
        this.username = username;
        this.password = password;

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL(xmlRpcUrl));
            this.config = config;
        } catch (MalformedURLException e) {
            logger.error("Cannot create url from provided xmlrpc url: {}", xmlRpcUrl);
            throw new BlogImportException("Cannot import blog due to problems with xmlrpc url", e);
        }
    }

    public List<Map> obtainItemsFromBlog(int numItemsToFetch, int startFrom) {
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);

        Object result = null;
        try {
            result = client.execute(config, "metaWeblog.getRecentPosts",
                    new Object[]{startFrom, username, password, numItemsToFetch});
        } catch (XmlRpcException e) {
            logger.error("Error while obtaining results from the wordpress blog", e);
            throw new BlogImportException("Error while obtaining results from the wordpress blog", e);
        }
        return items(result);
    }

    private List<Map> items(Object result) {
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
