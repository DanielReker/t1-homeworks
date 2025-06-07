package io.github.danielreker.t1homeworks.mapper;

import io.github.danielreker.t1homeworks.model.Client;
import io.github.danielreker.t1homeworks.model.dto.ClientDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {
    Client toEntity(ClientDto clientDto);

    ClientDto toClientDto(Client client);

    Client updateWithNull(ClientDto clientDto, @MappingTarget Client client);
}