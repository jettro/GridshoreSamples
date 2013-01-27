import nl.gridshore.elasticsearch.ElasticSearchGateway

/**
 * @author Jettro Coenradie
 */

ElasticSearchGateway gateway = new ElasticSearchGateway(null, null)

gateway.countAllDocuments()

gateway.queryIndex("groovy")

System.in.withReader {
    print 'input: '
    println it.readLine()
}

gateway.close()