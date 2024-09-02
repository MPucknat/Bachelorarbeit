package de.example.restdatasender;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.example.restdatasender.data.OCPI.LocationOCPI;
import de.example.restdatasender.data.OICP.LocationOICP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class FileService {

    private final ObjectMapper oBjectMapper;
    private final int numberOfEntries = 100000;

    public FileService(final ObjectMapper oBjectMapper) {
        this.oBjectMapper = oBjectMapper;
    }

    public List<LocationOCPI> readOCPIFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("ocpi_" + numberOfEntries + ".json");
        log.info(resource.getInputStream().toString());
        return oBjectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
    }

    public List<LocationOICP> readOICPFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("oicp_" + numberOfEntries + ".json");
        return oBjectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
    }
}
