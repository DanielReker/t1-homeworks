package io.github.danielreker.t1homeworks.service1.mapper;

import io.github.danielreker.t1homeworks.service1.model.TimeLimitExceedLog;
import io.github.danielreker.t1homeworks.service1.model.dto.TimeLimitExceedLogDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TimeLimitExceedLogMapper {
    TimeLimitExceedLog toEntity(TimeLimitExceedLogDto timeLimitExceedLogDto);

    TimeLimitExceedLogDto toTimeLimitExceedLogDto(TimeLimitExceedLog timeLimitExceedLog);
}