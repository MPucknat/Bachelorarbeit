package de.example.connector.ocpi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.example.VersionUtil;
import de.example.data.OCPI.LocationOCPI;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OCPIConnectorTask extends SourceTask {
    static final Logger log = LoggerFactory.getLogger(OCPIConnectorTask.class);

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private String serverAddress;
    private String topic;
    private int pollInterval;
    private int currentOffset;
    private long lastPoll;
    private boolean startInterval = false;

    @Override
    public void start(Map<String, String> map) {
        topic = map.get(OCPIConnectorConfig.TOPIC_SETTING);
        pollInterval = Integer.parseInt(map.get(OCPIConnectorConfig.POLL_INTERVAL_SETTING));
        serverAddress = map.get(OCPIConnectorConfig.SERVER_ADDRESS_SETTING);

        currentOffset = getOffset();
    }

    // Source: https://www.opencredo.com/blogs/kafka-connect-source-connectors-a-detailed-guide-to-connecting-to-what-you-love
    public int getOffset() {
        Map<String, Object> persistedMap = null;
        if (context != null && context.offsetStorageReader() != null) {
            persistedMap = context.offsetStorageReader().offset(Collections.singletonMap("url", serverAddress));
        }

        if (persistedMap != null) {
            return ((Long) persistedMap.get("offset")).intValue();
        }

        return 0;
    }

    private void waitForNextPoll() throws InterruptedException {
        long timeSinceLastPoll = System.currentTimeMillis() - lastPoll;
        if (timeSinceLastPoll <= pollInterval)
            Thread.sleep(pollInterval - timeSinceLastPoll);
        lastPoll = System.currentTimeMillis();
    }

    @Override
    public List<SourceRecord> poll() {
        ArrayList<SourceRecord> records = new ArrayList<>();
        Request request = new Request.Builder()
                .url(serverAddress + "/" + currentOffset)
                .build();

        try {
            if (startInterval)
                waitForNextPoll();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String data = response.body().string();
                if (data.isEmpty()) {
                    startInterval = true;
                    return null;
                }

                List<LocationOCPI> locations = mapper.readValue(data, new TypeReference<List<LocationOCPI>>() {});
                for (LocationOCPI location : locations) {
                    JsonNode jsonData = mapper.valueToTree(location);
                    Map<String, Integer> sourceOffset = Collections.singletonMap("offset", currentOffset);
                    Map<String, String> sourcePartition = Collections.singletonMap("url", serverAddress);
                    records.add(new SourceRecord(sourcePartition, sourceOffset, topic, null, jsonData.toString()));
                }
                currentOffset += locations.size();
            } else {
                log.error("Unsuccessful response: {} HTTP code: {}", response, response.code());
                return null;
            }

            response.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return records;
    }

    @Override
    public void stop() {
    }

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }
}