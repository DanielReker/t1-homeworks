package io.github.danielreker.t1homeworks.service2.service;

import io.github.danielreker.t1homeworks.service2.kafka.TransactionResultProducer;
import io.github.danielreker.t1homeworks.service2.model.Transaction;
import io.github.danielreker.t1homeworks.service2.model.dto.TransactionAcceptDto;
import io.github.danielreker.t1homeworks.service2.model.dto.TransactionResultDto;
import io.github.danielreker.t1homeworks.service2.model.enums.TransactionStatus;
import io.github.danielreker.t1homeworks.service2.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    private static final long TIME_PERIOD_MS = 3_600_000L;
    private static final long MAX_TRANSACTIONS = 5L;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionResultProducer transactionResultProducer;

    @InjectMocks
    private TransactionService transactionService;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    @Captor
    private ArgumentCaptor<TransactionResultDto> transactionResultDtoCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(transactionService, "timePeriodMs", TIME_PERIOD_MS);
        ReflectionTestUtils.setField(transactionService, "maxTransactionsInPeriod", MAX_TRANSACTIONS);
    }

    @Test
    void givenValidTransaction_whenProcessTransaction_thenStatusIsAccepted() {
        TransactionAcceptDto acceptDto = new TransactionAcceptDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now(),
                new BigDecimal("-50.00"),
                new BigDecimal("100.00")
        );

        when(transactionRepository.findAllByTimestampGreaterThanAndAccountId(any(Instant.class), eq(acceptDto.accountId())))
                .thenReturn(List.of());


        transactionService.processTransaction(acceptDto);


        verify(transactionRepository)
                .save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals(TransactionStatus.ACCEPTED, savedTransaction.getStatus());
        assertEquals(acceptDto.transactionId(), savedTransaction.getTransactionId());
        assertEquals(acceptDto.accountId(), savedTransaction.getAccountId());


        verify(transactionResultProducer, times(1))
                .sendTransactionResult(transactionResultDtoCaptor.capture());
        TransactionResultDto sentResult = transactionResultDtoCaptor.getValue();
        assertEquals(TransactionStatus.ACCEPTED, sentResult.getStatus());
        assertEquals(acceptDto.transactionId(), sentResult.getTransactionId());
    }

    @Test
    void givenInsufficientBalance_whenProcessTransaction_thenStatusIsRejected() {
        TransactionAcceptDto acceptDto = new TransactionAcceptDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now(),
                new BigDecimal("-150.00"),
                new BigDecimal("100.00")
        );

        when(transactionRepository.findAllByTimestampGreaterThanAndAccountId(any(Instant.class), eq(acceptDto.accountId())))
                .thenReturn(List.of(Transaction.builder().build()));


        transactionService.processTransaction(acceptDto);


        verify(transactionRepository)
                .save(transactionCaptor.capture());
        assertEquals(TransactionStatus.REJECTED, transactionCaptor.getValue().getStatus());

        verify(transactionResultProducer, times(1))
                .sendTransactionResult(transactionResultDtoCaptor.capture());
        assertEquals(TransactionStatus.REJECTED, transactionResultDtoCaptor.getValue().getStatus());
        assertEquals(acceptDto.transactionId(), transactionResultDtoCaptor.getValue().getTransactionId());
    }

    @Test
    void givenTransactionLimitReached_whenProcessTransaction_thenStatusIsBlocked() {
        TransactionAcceptDto acceptDto = new TransactionAcceptDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now(),
                new BigDecimal("-10.00"),
                new BigDecimal("1000.00")
        );

        List<Transaction> previousTransactions = IntStream.range(0, (int) MAX_TRANSACTIONS)
                .mapToObj(i -> Transaction.builder()
                        .transactionId(UUID.randomUUID())
                        .accountId(acceptDto.accountId())
                        .status(TransactionStatus.ACCEPTED)
                        .timestamp(Instant.now().minus(Duration.ofMinutes(i + 1)))
                        .build())
                .collect(Collectors.toList());

        when(transactionRepository.findAllByTimestampGreaterThanAndAccountId(any(Instant.class), eq(acceptDto.accountId())))
                .thenReturn(previousTransactions);


        transactionService.processTransaction(acceptDto);


        verify(transactionRepository)
                .save(transactionCaptor.capture());
        assertEquals(TransactionStatus.BLOCKED, transactionCaptor.getValue().getStatus());
        assertEquals(acceptDto.transactionId(), transactionCaptor.getValue().getTransactionId());

        int expectedMessageCount = previousTransactions.size() + 1;
        verify(transactionResultProducer, times(expectedMessageCount))
                .sendTransactionResult(transactionResultDtoCaptor.capture());

        List<TransactionResultDto> allSentResults = transactionResultDtoCaptor.getAllValues();
        assertEquals(expectedMessageCount, allSentResults.size());

        allSentResults.forEach(result -> assertEquals(TransactionStatus.BLOCKED, result.getStatus()));

        List<UUID> expectedTransactionIds = previousTransactions.stream()
                .map(Transaction::getTransactionId)
                .collect(Collectors.toList());
        expectedTransactionIds.add(acceptDto.transactionId());

        List<UUID> actualTransactionIds = allSentResults.stream()
                .map(TransactionResultDto::getTransactionId)
                .toList();

        assertTrue(actualTransactionIds.containsAll(expectedTransactionIds));
    }
}