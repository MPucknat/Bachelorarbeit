package de.example.restdatasender;

import de.example.restdatasender.data.OCPI.LocationOCPI;
import de.example.restdatasender.data.OICP.LocationOICP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
public class RestDataSender {

    List<LocationOCPI> locationOCPIS;
    List<LocationOICP> locationOICPs;

    private final int packageSize = 10000;

    @Autowired
    public RestDataSender(FileService fileService) throws IOException {
        locationOCPIS = fileService.readOCPIFile();
        locationOICPs = fileService.readOICPFile();
    }

    @GetMapping("/ocpi")
    public ResponseEntity<List<LocationOCPI>> getData() {
        return getOCPIOffsetData(0);
    }

    @GetMapping("/ocpi/{offset}")
    public ResponseEntity<List<LocationOCPI>> getOCPIOffsetData(@PathVariable int offset) {
        log.info("OCPI offset {}", offset);

        int newDataSize = packageSize;

        if(offset == locationOCPIS.size())
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

        if(offset + packageSize > locationOCPIS.size())
            newDataSize = locationOCPIS.size() - offset;

        return new ResponseEntity<>(locationOCPIS.subList(offset, offset + newDataSize), HttpStatus.OK);
    }

    @GetMapping("/oicp")
    public ResponseEntity<List<LocationOICP>> getOICPData() {
        return getOICPOffsetData(0);
    }

    @GetMapping("/oicp/{offset}")
    public ResponseEntity<List<LocationOICP>> getOICPOffsetData(@PathVariable int offset) {
        log.info("OICP offset {}", offset);

        int newDataSize = packageSize;

        if(offset == locationOICPs.size())
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

        if(offset + packageSize > locationOICPs.size())
            newDataSize = locationOICPs.size() - offset;

        return new ResponseEntity<>(locationOICPs.subList(offset, offset + newDataSize), HttpStatus.OK);
    }
}
