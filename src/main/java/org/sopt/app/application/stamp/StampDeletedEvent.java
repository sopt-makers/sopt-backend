package org.sopt.app.application.stamp;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.event.Event;

@Getter
@RequiredArgsConstructor
public class StampDeletedEvent extends Event {

    private final List<String> fileUrls;
}
