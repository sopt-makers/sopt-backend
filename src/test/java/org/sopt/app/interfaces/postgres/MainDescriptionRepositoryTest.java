// package org.sopt.app.interfaces.postgres;
//
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.sopt.app.common.config.QuerydslConfiguration;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.context.annotation.Import;
//
// @DataJpaTest
// @AutoConfigureTestDatabase(replace = Replace.NONE)
// @Import(QuerydslConfiguration.class)
// class MainDescriptionRepositoryTest {
//
//     @Autowired
//     private MainDescriptionRepository mainDescriptionRepository;
//
//     @Test
//     @DisplayName("SUCCESS_메인 문구 전체 조회")
//     void SUCCESS_findAll() {
//         Assertions.assertDoesNotThrow(() -> mainDescriptionRepository.findAll());
//     }
// }
