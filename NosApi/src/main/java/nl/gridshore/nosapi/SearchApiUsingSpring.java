package nl.gridshore.nosapi;

import nl.gridshore.nosapi.mapping.Article;
import nl.gridshore.nosapi.mapping.LatestArticle;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jettro Coenradie
 */
public class SearchApiUsingSpring {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        LatestArticle result = restTemplate.getForObject("http://open.nos.nl/v1/latest/article/key/{key}/output/json/category/sport/",
                LatestArticle.class, "");

        for (Article article : result.getItems().get(0)) {
            System.out.printf("Title : %s\n", article.getTitle());
            System.out.printf("Description : %s\n", article.getDescription());
            System.out.printf("Link : %s\n", article.getLink());
            System.out.printf("Published : %s\n", article.getPublished().toDateTime());
            System.out.printf("Last update : %s\n", article.getLastUpdate().toDateTime());
            System.out.printf("Thumbnail xs : %s\n", article.getThumbnail_xs());
            System.out.printf("Thumbnail s : %s\n", article.getThumbnail_s());
            System.out.printf("Thumbnail m : %s\n", article.getThumbnail_m());
            if (article.getKeywords().size() > 0) {
                System.out.printf("Keywords : ");
                for (String keyword:article.getKeywords()) {
                    System.out.printf("%s,",keyword);
                }
                System.out.printf("\n");
            }
            System.out.println("_________________________________");
        }
    }
}
