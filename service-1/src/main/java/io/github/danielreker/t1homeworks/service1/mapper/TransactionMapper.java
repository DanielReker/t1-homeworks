package io.github.danielreker.t1homeworks.service1.mapper;

import io.github.danielreker.t1homeworks.service1.model.Transaction;
import io.github.danielreker.t1homeworks.service1.model.dto.CreateTransactionRequest;
import io.github.danielreker.t1homeworks.service1.model.dto.TransactionDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {AccountMapper.class})
public interface TransactionMapper {
    Transaction toEntity(TransactionDto transactionDto);

    TransactionDto toTransactionDto(Transaction transaction);

    Transaction updateWithNull(TransactionDto transactionDto, @MappingTarget Transaction transaction);

    @Mapping(source = "accountId", target = "account.id")
    Transaction toEntity(CreateTransactionRequest createTransactionRequest);

    @Mapping(source = "account.id", target = "accountId")
    CreateTransactionRequest toCreateTransactionRequest(Transaction transaction);
}