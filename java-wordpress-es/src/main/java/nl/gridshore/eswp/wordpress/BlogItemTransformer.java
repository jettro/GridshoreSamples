package nl.gridshore.eswp.wordpress;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by jettrocoenradie on 19/11/14.
 */
public class BlogItemTransformer {
    public static BlogItem transform(Map map) {
        BlogItem blogItem = new BlogItem();
        blogItem.setId(Integer.parseInt((String) map.get("postid")));
        blogItem.setLink((String) map.get("permalink"));
        blogItem.setStatus((String) map.get("post_status"));
        blogItem.setKeywords(byComma((String) map.get("mt_keywords")));
        blogItem.setTitle((String) map.get("title"));
        blogItem.setCreatedOn((Date) map.get("dateCreated"));
        blogItem.setContent((String) map.get("description"));
        Object[] categories = (Object[]) map.get("categories");
        blogItem.setCategories(Arrays.copyOf(categories, categories.length, String[].class));
        blogItem.setAuthor((String) map.get("wp_author_display_name"));
        blogItem.setSlug((String) map.get("wp_slug"));

        return blogItem;
    }

    private static String[] byComma(String items) {
        String[] strings = items.split(",");
        for (int i = 0; i < strings.length; i++) {
            strings[i] = strings[i].trim();
        }
        return strings;
    }
}
