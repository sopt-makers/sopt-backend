package org.sopt.app.presentation.association;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppleAssociationController {

    @Value("${apple.app-id}")
    private String appleAppId;

    @GetMapping(value = "/.well-known/apple-app-site-association")
    public ResponseEntity<Map<String, Object>> getAppleAppSiteAssociation() {
        return ResponseEntity.ok(Map.of(
            "applinks", Map.of(
                "apps", List.of(),
                "details", List.of(
                    Map.of(
                        "appID", appleAppId,
                        "paths", List.of("*")
                    )
                )
            ))
        );
    }
}
