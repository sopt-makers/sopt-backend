package org.sopt.app.application.alert;

import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.enums.Parts;
import org.sopt.app.presentation.alert.dto.PartRspDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    public PartRspDTO findPart() {

        //ENUM 값을 통해 part 들을 받아온다.
        List<String> partString = new ArrayList<>();
        for(Parts part : Parts.values()){
            partString.add(part.toString());
        }

        return PartRspDTO.builder()
                .parts(partString)
                .build();
    }
}
