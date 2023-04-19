package org.sopt.app.application.stamp;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sopt.app.common.event.Event;

@Getter
@Setter
@NoArgsConstructor
public class StampDeletedEvent extends Event {

    private List<String> fileUrls;

    public StampDeletedEvent(List<String> fileUrls) {
        super();
        this.fileUrls = fileUrls;
    }
}
