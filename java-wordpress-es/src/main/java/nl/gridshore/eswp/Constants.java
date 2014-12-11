package nl.gridshore.eswp;

import java.util.Arrays;
import java.util.List;

/**
 * Interface for constants used in multiple runners.
 */
public interface Constants {
    // Elasticsearch configuration
    String CLUSTER_NAME = "jc-play";
    List<String> UNICAST_HOSTS = Arrays.asList("localhost:9300");
    String INDEX_NAME = "gridshore";
    String INDEX_TYPE = "blog";
    String REPOSITORY_LOCATION = "/Users/jettrocoenradie/temp/elasticbck";
    String REPOSITORY_NAME = "gridshore-file";
}
