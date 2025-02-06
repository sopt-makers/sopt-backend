package org.sopt.app.application.rank;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.app.application.soptamp.SoptampUserInfo;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CachedUserInfo implements Serializable{
    private String name;
    private String profileMessage;
    private String part;

    public static CachedUserInfo of(SoptampUserInfo userInfo){
        return new CachedUserInfo(userInfo.getNickname(), userInfo.getProfileMessage(), userInfo.getPart().getPartName());
    }
}
