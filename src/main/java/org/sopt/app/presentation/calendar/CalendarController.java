package org.sopt.app.presentation.calendar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.calendar.CalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(summary = "일정 전체 보기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/all")
    public ResponseEntity<AllCalendarResponse> getAllCalendar() {
        return ResponseEntity.ok(
            calendarService.getAllCurrentGenerationCalendar()
        );
    }
}
