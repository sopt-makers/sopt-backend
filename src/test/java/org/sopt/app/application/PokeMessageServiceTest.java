package org.sopt.app.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.poke.PokeMessageService;
import org.sopt.app.domain.entity.poke.PokeMessage;
import org.sopt.app.domain.enums.PokeMessageType;
import org.sopt.app.interfaces.postgres.PokeMessageRepository;

@ExtendWith(MockitoExtension.class)
class PokeMessageServiceTest {

    private static final String MESSAGES_HEADER_FOR_POKE = "함께 보낼 메시지를 선택해주세요";
    private static final String MESSAGES_HEADER_FOR_REPLY = "답장하고 싶은 메시지를 선택해주세요";

    @Mock
    private PokeMessageRepository pokeMessageRepository;

    @InjectMocks
    private PokeMessageService pokeMessageService;


    @Test
    @DisplayName("SUCCESS_PokeMessageType으로 메세지 헤더 조회 REPLY_NEW")
    void SUCCESS_getMessagesHeaderCommentReplyNew() {
        assertEquals(MESSAGES_HEADER_FOR_REPLY, pokeMessageService.getMessagesHeaderComment("replyNew"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pokeSomeone", "pokeFriend"})
    @DisplayName("SUCCESS_PokeMessageType으로 메세지 헤더 조회 REPLY_NEW 제외")
    void SUCCESS_getMessagesHeaderComment(String type) {
        assertEquals(MESSAGES_HEADER_FOR_POKE, pokeMessageService.getMessagesHeaderComment(type));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pokeSomeone", "pokeFriend", "replyNew"})
    @DisplayName("SUCCESS_PokeMessageType으로 랜덤 메세지 조회")
    void SUCCESS_pickRandomMessageByTypeOf(String type) {
        List<PokeMessage> pokeMessageList = List.of(PokeMessage.builder().id(1L).content("content").build());

        when(pokeMessageRepository.findAllByType(any())).thenReturn(pokeMessageList);

        assertEquals(pokeMessageList, pokeMessageService.pickRandomMessageByTypeOf(type));
    }

    @Test
    void SUCCESS_getFixedMessage() {
        PokeMessage fixedMessage = PokeMessage.builder()
                .id(0L).content("콕 \uD83D\uDC48").type(PokeMessageType.POKE_ALL)
                .build();

        PokeMessage result = pokeMessageService.getFixedMessage();

        assertEquals(fixedMessage.getId(), result.getId());
        assertEquals(fixedMessage.getContent(), result.getContent());
    }
}
