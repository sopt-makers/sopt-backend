package org.sopt.app.application.stamp;

import java.util.List;
import lombok.Getter;
import org.sopt.app.common.event.Event;

@Getter
public class StampDeletedEvent extends Event {

    private List<String> fileUrls;

    public StampDeletedEvent(List<String> fileUrls) {
        super();
        this.fileUrls = fileUrls;
    }
}
