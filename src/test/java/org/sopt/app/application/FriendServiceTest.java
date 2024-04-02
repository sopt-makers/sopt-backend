package org.sopt.app.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.app.application.poke.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FriendServiceTest {

    @Autowired
    private FriendService friendService;

    @Test
    @DisplayName("200 - getIsNewUserSuccess")
    void getIsNewUserSuccess() {
        Assertions.assertEquals(true, friendService.getIsNewUser(1L));
    }
}