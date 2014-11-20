package nl.gridshore.eswp.elasticsearch;

import nl.gridshore.eswp.wordpress.BlogItem;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Class that transforms a BlogItem into an IndexRequest for elasticsearch that can be used by a bulk processor to
 * index the document represented by the BlogItem object.
 */
public class BlogItemElasticTransformer {
    private final static Logger logger = LoggerFactory.getLogger(BlogItemElasticTransformer.class);

    private String indexName = "gridshore";
    private String indexType = "blog";

    public BlogItemElasticTransformer(String indexName, String indexType) {
        this.indexName = indexName;
        this.indexType = indexType;
    }

    public IndexRequest transform(BlogItem blogItem) {
        IndexRequest request = new IndexRequest(indexName, indexType, String.valueOf(blogItem.getId()));
        XContentBuilder builder;
        try {
            builder = jsonBuilder()
                    .startObject()
                    .field("content", blogItem.getContent())
                    .field("author", blogItem.getAuthor())
                    .field("categories", blogItem.getCategories())
                    .field("createdOn", blogItem.getCreatedOn())
                    .field("keywords", blogItem.getKeywords())
                    .field("permalink", blogItem.getLink())
                    .field("slug", blogItem.getSlug())
                    .field("status", blogItem.getStatus())
                    .field("title", blogItem.getTitle())
                    .endObject();
        } catch (IOException e) {
            logger.error("Something went wrong when creating the elastic document.", e);
            return null;
        }
        request.source(builder);
        return request;
    }
}
