package nl.gridshore.eswp.elasticsearch;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Utility class to provide methods for creating a settings object and a mappings object.
 */
public class SettingsMappingsBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SettingsMappingsBuilder.class);

    /**
     * Creates a very basic settings object
     * @return String containing the settings for an elasticsearch index
     */
    public static String createSettingsString() {
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

    /**
     * returns a String containing the mapping for an elasticsearch type.
     * @return String containing the mapping
     */
    public static String createMappingsString() {
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

}
