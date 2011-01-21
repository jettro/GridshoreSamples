package nl.gridshore.nosapi;

import nl.gridshore.nosapi.mapping.VersionWrapper;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jettro Coenradie
 */
public class DataProviderImpl implements DataProvider {
    private String serverBaseUrl = "http://open.nos.nl/v1/";

    private final String apiKey;
    private final RestTemplate restTemplate;

    public DataProviderImpl(String apiKey) {
        this(apiKey, new RestTemplate());
        restTemplate.setErrorHandler(new NosApiResponseErrorHandler());
    }

    public DataProviderImpl(String apiKey, RestTemplate restTemplate) {
        Assert.hasText(apiKey);
        Assert.notNull(restTemplate);
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @Override
    public Version obtainVersion() {
        VersionWrapper versionWrapper = restTemplate.getForObject(
                serverBaseUrl + "index/version/key/{apikey}/output/json", VersionWrapper.class, apiKey);
        return new Version(
                versionWrapper.getVersions().get(0).getBuild(),
                versionWrapper.getVersions().get(0).getVersion());
    }
}
