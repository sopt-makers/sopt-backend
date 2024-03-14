package org.sopt.app.interfaces.postgres;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MainDescriptionTest {

    @Autowired
    private MainDescriptionRepository mainDescriptionRepository;

    @Test
    @DisplayName("200 - findAllSuccessWithSizeOne")
    void findAllSuccessWithSizeOne() {
        Assertions.assertEquals(1, mainDescriptionRepository.findAll().size());
    }
}
