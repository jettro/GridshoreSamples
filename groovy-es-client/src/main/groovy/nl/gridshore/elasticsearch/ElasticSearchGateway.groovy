package nl.gridshore.elasticsearch

import nl.gridshore.wordpress.BlogItem
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.groovy.common.xcontent.GXContentBuilder
import org.elasticsearch.groovy.node.GNode
import org.elasticsearch.groovy.node.GNodeBuilder
import org.elasticsearch.search.SearchHit

import static org.elasticsearch.groovy.node.GNodeBuilder.nodeBuilder

/**
 * @author Jettro Coenradie
 */
class ElasticSearchGateway {
    GNode node

    ElasticSearchGateway() {
        GXContentBuilder.rootResolveStrategy = Closure.DELEGATE_FIRST; // required to use a closure as settings

        GNodeBuilder nodeBuilder = nodeBuilder();
        nodeBuilder.settings {
            node {
                client = true
            }

            cluster {
                name = "jc-elasticsearch"
            }
        }

        node = nodeBuilder.node()
    }

    public queryIndex(theTerm) {
        def search = node.client.search {
            indices : "gridshore"
            types : "blog"
            source {
                query {
                    term(_all: theTerm)
                }
            }
        }

        search.response.hits.each {SearchHit hit ->
            println "Got hit $hit.id from $hit.index/$hit.type with title $hit.source.title"
        }
    }

    public countAllDocuments() {
        def count = node.client.count {
            indices : "gridshore"
            parameterTypes : "blog"
        }

        println "Number of found blog items : $count.response.count"
    }

    public indexBlogItem(BlogItem blogItem) {
        def future = node.client.index {
            index = "gridshore"
            type = "blog"
            source {
                blogId = blogItem.id
                link = blogItem.link
                status = blogItem.status
                keywords = blogItem.keywords
                title = blogItem.title
                createdOn_date = blogItem.createdOn
                content = blogItem.content
                categories = blogItem.categories
                author = blogItem.author
                slug = blogItem.slug
            }
        }
        // a listener can be added to the future
        future.success = {IndexResponse response ->
            println "Indexed $response.index/$response.type/$response.id"
        }
    }

    public close() {
        node.stop().close()
    }
}
