import org.elasticsearch.groovy.node.GNode
import org.elasticsearch.groovy.node.GNodeBuilder
import org.elasticsearch.search.SearchHit

import static org.elasticsearch.groovy.node.GNodeBuilder.nodeBuilder

org.elasticsearch.groovy.common.xcontent.GXContentBuilder.rootResolveStrategy = Closure.DELEGATE_FIRST;

// Start the connection

GNodeBuilder nodeBuilder = nodeBuilder();
nodeBuilder.settings {
    node {
        client = true
    }

    cluster {
        name = "jc-elasticsearch"
    }
}

GNode node = nodeBuilder.node()

// Do something with the connection
def search = node.client.search {
    indices "ro"
    types "rijksoverheid"
    source {
        query {
            term(title_nl: "drinken")
        }
    }
}

search.response.hits.each {SearchHit hit ->
    println "Got hit $hit.id from $hit.index/$hit.type"
    println "Title: $hit.source.title_nl"
}

//System.in.withReader {
//    print 'input: '
//    println it.readLine()
//}

// close the connection

node.stop().close()