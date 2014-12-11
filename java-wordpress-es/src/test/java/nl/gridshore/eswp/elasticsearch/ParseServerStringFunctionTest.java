package nl.gridshore.eswp.elasticsearch;

import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParseServerStringFunctionTest {

    @Test
    public void testParseDefault() throws Exception {
        InetSocketTransportAddress result = ParseServerStringFunction.parse("10.10.10.10");
        assertEquals("inet[/10.10.10.10:9300]", result.toString());
    }

    @Test
    public void testParseCustomPort() throws Exception {
        InetSocketTransportAddress result = ParseServerStringFunction.parse("10.10.10.10:9333");
        assertEquals("inet[/10.10.10.10:9333]", result.toString());
    }

}