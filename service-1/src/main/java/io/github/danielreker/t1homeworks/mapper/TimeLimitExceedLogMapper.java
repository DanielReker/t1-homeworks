package io.github.danielreker.t1homeworks.mapper;

import io.github.danielreker.t1homeworks.model.TimeLimitExceedLog;
import io.github.danielreker.t1homeworks.model.dto.TimeLimitExceedLogDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TimeLimitExceedLogMapper {
    TimeLimitExceedLog toEntity(TimeLimitExceedLogDto timeLimitExceedLogDto);

    TimeLimitExceedLogDto toTimeLimitExceedLogDto(TimeLimitExceedLog timeLimitExceedLog);
}