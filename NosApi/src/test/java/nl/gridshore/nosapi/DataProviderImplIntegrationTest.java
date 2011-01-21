package nl.gridshore.nosapi;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author Jettro Coenradie
 */
public class DataProviderImplIntegrationTest {
    private static String key;

    @BeforeClass
    public static void loadProperties() throws IOException {
        Properties props = new Properties();
        InputStream inputStream = DataProviderImplIntegrationTest.class.getClassLoader()
            .getResourceAsStream("key.properties");
        props.load(inputStream);
        key = props.getProperty("key");
    }


    @Test
    public void testObtainVersion() throws Exception {
        Version version;
        DataProvider dataProvider = new DataProviderImpl(key);
        version = dataProvider.obtainVersion();
        assertEquals("v1", version.getVersion());
        assertEquals("0.0.1", version.getBuild());
    }
}
