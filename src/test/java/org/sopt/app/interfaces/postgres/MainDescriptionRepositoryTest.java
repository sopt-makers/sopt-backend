package org.sopt.app.interfaces.postgres;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MainDescriptionRepositoryTest {

    @Autowired
    private MainDescriptionRepository mainDescriptionRepository;

    @Test
    @DisplayName("SUCCESS - findAll")
    void success_findAll() {
        System.out.println(mainDescriptionRepository.findAll());
    }
}
