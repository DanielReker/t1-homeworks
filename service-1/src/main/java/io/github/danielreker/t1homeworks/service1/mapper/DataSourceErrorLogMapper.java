package io.github.danielreker.t1homeworks.service1.mapper;

import io.github.danielreker.t1homeworks.service1.model.DataSourceErrorLog;
import io.github.danielreker.t1homeworks.service1.model.dto.DataSourceErrorLogDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface DataSourceErrorLogMapper {
    DataSourceErrorLog toEntity(DataSourceErrorLogDto dataSourceErrorLogDto);

    DataSourceErrorLogDto toDataSourceErrorLogDto(DataSourceErrorLog dataSourceErrorLog);
}