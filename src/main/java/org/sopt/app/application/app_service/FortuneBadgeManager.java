package org.sopt.app.application.app_service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("fortuneBadgeManager")
public class FortuneBadgeManager implements AppServiceBadgeManager {

}
