package org.sopt.app.presentation.user;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MySoptLogTest {

    @Test
    @DisplayName("ofActive는 전달된 isAppjamMode를 그대로 반영한다")
    void ofActive_reflectsIsAppjamMode() {
        UserResponse.MySoptLog log = UserResponse.MySoptLog.ofActive(
            true,   // isAppjamMode
            false,  // isAppjamParticipant
            true,   // isFortuneChecked
            "오늘의 운세",
            1, 2, 3, 4, 5, 6, 7, 8
        );

        assertTrue(log.isAppjamMode());
        assertTrue(log.isActive());
        assertFalse(log.isAppjamParticipant());
    }

    @Test
    @DisplayName("ofInactiveNonAppjam은 isAppjamMode만 반영하고 isActive/isAppjamParticipant는 false다")
    void ofInactiveNonAppjam_reflectsIsAppjamMode() {
        UserResponse.MySoptLog log = UserResponse.MySoptLog.ofInactiveNonAppjam(
            true,   // isAppjamMode
            false,  // isFortuneChecked
            "text",
            10, 11, 12, 13
        );

        assertTrue(log.isAppjamMode());
        assertFalse(log.isActive());
        assertFalse(log.isAppjamParticipant());
    }

    @Test
    @DisplayName("record 직렬화 시 JSON 키에 is 접두어가 그대로 유지된다 (isAppjamMode)")
    void serialization_keepsIsPrefix() {
        UserResponse.MySoptLog log = UserResponse.MySoptLog.ofActive(
            true, false, true, "오늘의 운세", 1, 2, 3, 4, 5, 6, 7, 8);

        JsonNode json = new ObjectMapper().valueToTree(log);

        assertTrue(json.has("isAppjamMode"));   // record는 컴포넌트명 그대로 → is 유지
        assertFalse(json.has("appjamMode"));    // is 벗겨진 형태는 없음
    }
}
