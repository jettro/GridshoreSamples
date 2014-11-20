package nl.gridshore.eswp.wordpress;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by jettrocoenradie on 19/11/14.
 */
public class BlogItem {
    private int id;
    private String link;
    private String status;
    private String[] keywords;
    private String title;
    private Date createdOn;
    private String content;
    private String[] categories;
    private String author;
    private String slug;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return "BlogItem{" +
                "id=" + id +
                ", link='" + link + '\'' +
                ", status='" + status + '\'' +
                ", keywords='" + keywords + '\'' +
                ", title='" + title + '\'' +
                ", createdOn=" + createdOn +
                ", content='" + content + '\'' +
                ", categories=" + Arrays.toString(categories) +
                ", author='" + author + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }
}
