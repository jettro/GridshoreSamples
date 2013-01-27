import nl.gridshore.elasticsearch.ElasticSearchGateway
import nl.gridshore.wordpress.BlogItem
import nl.gridshore.wordpress.WordpressReader

/**
 * @author Jettro Coenradie
 */

def rpcUrl = "http://www.gridshore.nl/xmlrpc.php"
def username = "admin"
def password = "Alwin245"

def reader = new WordpressReader(rpcUrl,username,password)

def posts = reader.obtainMostRecentPosts(100)

ElasticSearchGateway gateway = new ElasticSearchGateway()

posts.each {BlogItem item ->
    println item.title
    gateway.indexBlogItem(item)
}

System.in.withReader {
    print 'input: '
    println it.readLine()
}

gateway.close()