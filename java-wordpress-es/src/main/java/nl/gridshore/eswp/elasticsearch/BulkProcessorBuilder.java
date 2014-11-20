package nl.gridshore.eswp.elasticsearch;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * This class can be used to create a BulkProcessor with some defaults using a logging based listener. Do not
 * forget to close the BulkProcessor when you are done, you can use the close() method or the special wait and close
 * method:
 * <code>bulkProcessor.awaitClose(10, TimeUnit.SECONDS);</code>
 */
public class BulkProcessorBuilder {
    private static final Logger logger = LoggerFactory.getLogger(BulkProcessorBuilder.class);

    public static final int BULK_ACTIONS_THRESHOLD = 100;
    public static final int BULK_CONCURRENT_REQUESTS = 1;
    public static final int BULK_FLUSH_DURATION = 30;

    public static BulkProcessor build(Client client) {
        return build(client,BULK_ACTIONS_THRESHOLD, BULK_CONCURRENT_REQUESTS, BULK_FLUSH_DURATION);
    }

    public static BulkProcessor build(Client client, int threshold) {
        return build(client,threshold, BULK_CONCURRENT_REQUESTS, BULK_FLUSH_DURATION);
    }

    public static BulkProcessor build(Client client, int threshold, int concurrentRequests, int flushDuration) {
        return BulkProcessor.builder(client,
                createLoggingBulkProcessorListener()).setBulkActions(threshold)
                .setConcurrentRequests(concurrentRequests)
                .setFlushInterval(createFlushIntervalTime(flushDuration))
                .build();

    }

    private static BulkProcessor.Listener createLoggingBulkProcessorListener() {
        return new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                logger.info("Going to execute new bulk composed of {} actions", request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                logger.info("Executed bulk composed of {} actions", request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                logger.warn("Error executing bulk", failure);
            }
        };
    }

    private static TimeValue createFlushIntervalTime(int flushDuration) {
        return new TimeValue(flushDuration, TimeUnit.SECONDS);
    }

}
