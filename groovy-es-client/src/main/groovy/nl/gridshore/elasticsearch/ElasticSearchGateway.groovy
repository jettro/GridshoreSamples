package nl.gridshore.elasticsearch

import nl.gridshore.wordpress.BlogItem
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
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
    String indexValue;
    String typeValue;

    GNode node

    ElasticSearchGateway(String index, String type) {
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
        this.indexValue = index
        this.typeValue = type
    }

    public queryIndex(theTerm) {
        def search = node.client.search {
            indices: indexValue
            types: typeValue
            source {
                query {
                    term(_all: theTerm)
                }
            }
        }

        search.response.hits.each { SearchHit hit ->
            println "Got hit $hit.id from $hit.index/$hit.type with title $hit.source.title"
        }
    }

    public deleteIndex() {
        def response = node.client.admin.indices.prepareDelete(indexValue).execute().get()
        if (response.acknowledged) {
            println "The index is removed"
        } else {
            println "The index could not be removed"
        }
    }

    public countAllDocuments() {
        def count = node.client.count {
            indices: indexValue
            parameterTypes: typeValue
        }

        println "Number of found blog items : $count.response.count"
    }

    public createIndex() {
        def future = node.client.admin.indices.create {
            index = this.indexValue
            settings {
                number_of_shards = "1"
                analysis {
                    analyzer {
                        comma {
                            type = "custome"
                            tokenizer = "bycomma"
                            filter = ["nowhite"]
                        }
                    }
                    tokenizer {
                        bycomma {
                            type = "pattern"
                            pattern = ","
                        }
                    }
                    filter {
                        nowhite {
                            type = "trim"
                        }
                    }
                }
            }
            mapping this.typeValue, {
                "${this.typeValue}" {
                    properties {
                        keywords {
                            type = "string"
                            analyzer = "comma"
                        }
                        categories {
                            type = "string"
                            analyzer = "comma"
                        }
                    }
                }
            }

        }

        future.success = { CreateIndexResponse response ->
            println "Index is created"
        }

        future.failure = {
            println "ERROR creating index $it"
        }

    }

    public indexBlogItem(BlogItem blogItem) {
        def future = node.client.index {
            index = this.indexValue
            type = this.typeValue
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
        future.success = { IndexResponse response ->
            println "Indexed $response.index/$response.type/$response.id"
        }

        future.failure = {
            println "ERROR $it"
        }
    }

    public close() {
        node.stop().close()
    }
}
