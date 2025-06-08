package io.github.danielreker.t1homeworks.service1.mapper;

import io.github.danielreker.t1homeworks.service1.model.dto.AccountDto;
import io.github.danielreker.t1homeworks.service1.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {ClientMapper.class})
public interface AccountMapper {
    Account toEntity(AccountDto accountDto);

    AccountDto toAccountDto(Account account);

    Account updateWithNull(AccountDto accountDto, @MappingTarget Account account);
}