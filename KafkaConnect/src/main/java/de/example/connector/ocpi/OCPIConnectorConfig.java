package de.example.connector.ocpi;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.Map;


public class OCPIConnectorConfig extends AbstractConfig {
    public static final String SERVER_ADDRESS_SETTING = "server-address";
    public static final String TOPIC_SETTING = "topic";
    public static final String POLL_INTERVAL_SETTING = "poll-interval";

    public OCPIConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
        super(config, parsedConfig);
    }

    public OCPIConnectorConfig(Map<String, String> parsedConfig) {
        this(conf(), parsedConfig);
    }

    public static ConfigDef conf() {
        return new ConfigDef()
                .define(SERVER_ADDRESS_SETTING, Type.STRING, Importance.HIGH, "The address to the REST server.")
                .define(TOPIC_SETTING, Type.STRING, Importance.HIGH, "The destination topic where all the data is being saved to.")
                .define(POLL_INTERVAL_SETTING, Type.INT, Importance.HIGH, "The poll interval in milliseconds.");
    }
}
