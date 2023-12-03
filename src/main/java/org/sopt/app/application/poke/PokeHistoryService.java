package org.sopt.app.application.poke;

import lombok.RequiredArgsConstructor;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokeHistoryService {

    private final PokeHistoryRepository pokeHistoryRepository;

    public Boolean isNewPoker(Long userId) {
        return pokeHistoryRepository.findAllByPokerId(userId).isEmpty();
    }
}
