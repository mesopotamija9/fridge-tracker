package dev.brkovic.fridge.tracker.api;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/docs")
public class OpenApiImpl {

    @GetMapping(value = "/openapi.yaml", produces = "application/yaml")
    public ResponseEntity<Resource> getOpenApiYaml() {
        ClassPathResource openapiYaml = new ClassPathResource("openapi.yaml");
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/yaml"))
                .body(openapiYaml);
    }
}
