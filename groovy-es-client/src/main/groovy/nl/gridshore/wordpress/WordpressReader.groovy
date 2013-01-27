package nl.gridshore.wordpress

import groovy.net.xmlrpc.XMLRPCServerProxy

/**
 * This class exposes a few methods that help in reading items from a wordpress blog. The wordpress xmlrpc api is
 * used to get information from the blog. You initialize this object with the url to the xmlrpc api and the required
 * username and password.
 *
 * @author Jettro Coenradie
 */
class WordpressReader {
    private String xmlrpcUrl
    private String username
    private String password

    private XMLRPCServerProxy serverProxy;

    def WordpressReader(xmlrpcUrl, username, password) {
        this.xmlrpcUrl = xmlrpcUrl
        this.username = username
        this.password = password

        serverProxy = new XMLRPCServerProxy(xmlrpcUrl)
        serverProxy.setBasicAuth(username, password)
    }

    def obtainMostRecentPosts(int number = 10) {
        def posts = []
        def foundPosts = serverProxy.metaWeblog.getRecentPosts(1, username, password, number)
        foundPosts.each {post ->
            def blogItem = new BlogItem()
            blogItem.id = post['postid']
            blogItem.link = post['permaLink']
            blogItem.status = post['post_status']
            blogItem.keywords = post['mt_keywords']
            blogItem.title = post['title']
            blogItem.createdOn = post['dateCreated']
            blogItem.content = post['description']
            blogItem.categories = post['categories']
            blogItem.author = post['wp_author_display_name']
            blogItem.slug = post['wp_slug']
            posts.add(blogItem)
        }
        return posts
    }
}
