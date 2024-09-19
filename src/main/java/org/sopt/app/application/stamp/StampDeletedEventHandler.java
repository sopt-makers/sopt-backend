package org.sopt.app.application.stamp;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.s3.S3Service;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StampDeletedEventHandler {

    private final S3Service s3Service;

    @EventListener(StampDeletedEvent.class)
    public void handle(StampDeletedEvent event) {
        s3Service.deleteFiles(event.getFileUrls(), "stamp");
    }
}