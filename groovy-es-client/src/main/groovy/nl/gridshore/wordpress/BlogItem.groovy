package nl.gridshore.wordpress

/**
 * Value object for items read from the blog, complete content of the blog item is stored in this object.
 *
 * @author Jettro Coenradie
 */
class BlogItem {
    def id
    def link
    def status
    def keywords
    def title
    def createdOn
    def content
    def categories
    def author
    def slug
}
