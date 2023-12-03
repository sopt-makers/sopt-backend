package org.sopt.app.presentation.poke;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.poke.PokeService;
import org.sopt.app.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/poke")
@SecurityRequirement(name = "Authorization")
public class PokeController {

    private final PokeService pokeService;

    @Operation(summary = "찌르기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping("/{userId}")
    public ResponseEntity orderPoke(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") Long pokedUserId,
            @RequestBody PokeRequest.PokeMessageRequest messageRequest
    ) {
        pokeService.poke(user.getId(), pokedUserId, messageRequest.getMessageId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(null);
    }

}
