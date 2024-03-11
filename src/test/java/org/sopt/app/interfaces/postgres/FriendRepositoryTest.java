package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FriendRepositoryTest {

    @Autowired
    private FriendRepository friendRepository;

    @Test
    @DisplayName("200 - findAllByFriendUserId")
    void findAllByFriendUserIdSuccess() {
        Assertions.assertEquals(List.of(), friendRepository.findAllByFriendUserId(1L));
    }

    @Test
    @DisplayName("200 - findAllByUserIdAndFriendUserIdIn")
    void findAllByUserIdAndFriendUserIdInSuccess() {
        Assertions.assertEquals(List.of(), friendRepository.findAllByUserIdAndFriendUserIdIn(1L, List.of(2L)));
    }

}