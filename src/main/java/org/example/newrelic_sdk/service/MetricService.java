package org.example.newrelic_sdk.service;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.OkHttpPoster;
import com.newrelic.telemetry.SenderConfiguration;
import com.newrelic.telemetry.TelemetryClient;
import com.newrelic.telemetry.metrics.Count;
import com.newrelic.telemetry.metrics.MetricBatch;
import com.newrelic.telemetry.metrics.MetricBatchSender;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Service
public class MetricService {

    private static final String METRIC_API_URL = "https://metric-api.eu.newrelic.com/metric/v1";
    private final TelemetryClient telemetryClient;

    public MetricService() throws MalformedURLException {
        String apiKey = System.getenv("NEWRELIC_API_KEY");
        this.telemetryClient = creteClient(apiKey);
    }

    public void recordCustom(int value) {
        Attributes attributes = new Attributes();
        attributes.put("service.name", "New Relic_SDK");
        Count count = new Count("Test_count", value, System.currentTimeMillis() - 10, System.currentTimeMillis(), new Attributes());
        this.telemetryClient.sendBatch(new MetricBatch(List.of(count), attributes));
        telemetryClient.shutdown();
    }

    private static TelemetryClient creteClient(String apiKey)
            throws MalformedURLException {
        SenderConfiguration config =
                MetricBatchSender.configurationBuilder()
                        .apiKey(apiKey)
                        .endpoint(new URL(METRIC_API_URL))
                        .useLicenseKey(true)
                        .httpPoster(new OkHttpPoster(Duration.ofSeconds(2)))
                        .build();
        MetricBatchSender sender = MetricBatchSender.create(config);
        return new TelemetryClient(sender, null, null, null);
    }
}
