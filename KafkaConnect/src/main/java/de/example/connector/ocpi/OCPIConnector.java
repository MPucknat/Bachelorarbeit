package de.example.connector.ocpi;

import de.example.VersionUtil;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OCPIConnector extends SourceConnector {
    private final static Logger log = LoggerFactory.getLogger(OCPIConnector.class);
    private Map<String, String> props;

    @Override
    public void start(Map<String, String> props) {
        this.props = props;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return OCPIConnectorTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        for(int i = 0; i < maxTasks; i++)
            configs.add(props);

        return configs;
    }

    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return OCPIConnectorConfig.conf();
    }

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }
}
