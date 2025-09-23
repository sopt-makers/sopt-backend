 package org.sopt.app.application;

 import static org.assertj.core.api.Assertions.assertThat;
 import static org.assertj.core.api.Assertions.assertThatThrownBy;
 import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
 import static org.mockito.ArgumentMatchers.anyString;

 import java.util.Collections;
 import java.util.List;
 import java.util.Optional;
 import org.assertj.core.groups.Tuple;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.Mockito;
 import org.mockito.junit.jupiter.MockitoExtension;
 import org.sopt.app.application.soptamp.SoptampUserFinder;
 import org.sopt.app.application.soptamp.SoptampUserInfo;
 import org.sopt.app.common.exception.BadRequestException;
 import org.sopt.app.common.fixtures.SoptampUserFixture;
 import org.sopt.app.common.response.ErrorCode;
 import org.sopt.app.domain.entity.soptamp.SoptampUser;
 import org.sopt.app.domain.enums.Part;
 import org.sopt.app.interfaces.postgres.SoptampUserRepository;
 import org.springframework.test.util.ReflectionTestUtils;

 @ExtendWith(MockitoExtension.class)
 class SoptampUserFinderTest {

     @Mock
     private SoptampUserRepository soptampUserRepository;

     @InjectMocks
     private SoptampUserFinder soptampUserFinder;

     @Test
     @DisplayName("SUCCESS_닉네임으로 유저 조회")
     void SUCCESS_findByNickname() {
         //given
         final Long id = 1L;
         final Long userId = 1L;
         final String nickname = "서버테스터";

         SoptampUser soptampUser = SoptampUserFixture.getSoptampUser(id, userId, nickname);

         //when
         Mockito.when(soptampUserRepository.findUserByNickname(soptampUser.getNickname())).thenReturn(Optional.of(soptampUser));

         //then
         SoptampUserInfo expected = SoptampUserInfo.of(soptampUser);

         assertThat(soptampUserFinder.findByNickname(nickname)).usingRecursiveComparison().isEqualTo(expected);
     }

     @Test
     @DisplayName("FAIL_닉네임으로 유저 조회 실패시 BadRequestException, USER_NOT_FOUND 예외 발생")
     void FAIL_findByNickname() {
         //given
         final String anyNickname = anyString();

         //when
         Mockito.when(soptampUserRepository.findUserByNickname(anyNickname)).thenReturn(Optional.empty());

         //then
         assertThatThrownBy(() -> soptampUserFinder.findByNickname(anyNickname))
             .isInstanceOf(BadRequestException.class)
             .satisfies(e -> {
                 BadRequestException exception = (BadRequestException) e;
                 assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
             });
     }

     @Test
     @DisplayName("SUCCESS_파트와 기수로 유저 목록 조회 성공")
     void SUCCESS_findAllByPartAndCurrentGeneration() {
         // given
         final Part part = Part.SERVER;
         ReflectionTestUtils.setField(soptampUserFinder, "currentGeneration", SoptampUserFixture.CURRENT_GENERATION);

         List<SoptampUser> serverPartSoptampUser = SoptampUserFixture.SERVER_PART_SOPTAMP_USER;

         Mockito.when(soptampUserRepository.findAllByNicknameStartingWithAndGeneration(part.getPartName(), SoptampUserFixture.CURRENT_GENERATION))
             .thenReturn(serverPartSoptampUser);

         // when
         List<SoptampUserInfo> result = soptampUserFinder.findAllByPartAndCurrentGeneration(part);

         // then
         assertThat(result).hasSize(serverPartSoptampUser.size());

         List<SoptampUserInfo> expected = serverPartSoptampUser.stream()
             .map(SoptampUserInfo::of)
             .toList();

         assertThat(result).usingRecursiveComparison()
             .ignoringCollectionOrder()
             .isEqualTo(expected);
     }

     @Test
     @DisplayName("SUCCESS_파트와 기수에 해당하는 유저가 없을 때 빈 리스트 반환")
     void SUCCESS_findAllByPartAndCurrentGeneration_whenEmpty() {
         // given
         final Part part = Part.SERVER;
         final Long generation = 1L;

         ReflectionTestUtils.setField(soptampUserFinder, "currentGeneration", generation);

         Mockito.when(soptampUserRepository.findAllByNicknameStartingWithAndGeneration(part.getPartName(), generation))
             .thenReturn(Collections.emptyList());

         // when
         List<SoptampUserInfo> result = soptampUserFinder.findAllByPartAndCurrentGeneration(part);

         // then
         assertThat(result)
             .isNotNull()
             .isEmpty();
     }
 }

