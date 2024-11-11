package org.sopt.app.application.app_service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("soptampBadgeManager")
public class SoptampBadgeManager implements AppServiceBadgeManager {

}
