package org.sopt.app.domain.enums;

import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;

import jakarta.persistence.*;
import java.util.Objects;

@Converter
public class PokeMessageTypeConverter implements AttributeConverter<PokeMessageType, String> {
    @Override
    public String convertToDatabaseColumn(PokeMessageType attribute) {
        if (Objects.isNull(attribute)) {
            throw new BadRequestException(ErrorCode.POKE_MESSAGE_MUST_NOT_BE_NULL);
        }
        return attribute.toString();
    }

    @Override
    public PokeMessageType convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData) || dbData.isEmpty()) {
            throw new BadRequestException(ErrorCode.POKE_MESSAGE_MUST_NOT_BE_NULL);
        }
        return PokeMessageType.valueOf(dbData);
    }
}
