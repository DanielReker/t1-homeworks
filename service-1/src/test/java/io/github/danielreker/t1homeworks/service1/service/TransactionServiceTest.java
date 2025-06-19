package io.github.danielreker.t1homeworks.service1.service;

import io.github.danielreker.t1homeworks.service1.kafka.TransactionAcceptProducer;
import io.github.danielreker.t1homeworks.service1.mapper.TransactionMapper;
import io.github.danielreker.t1homeworks.service1.model.Account;
import io.github.danielreker.t1homeworks.service1.model.Client;
import io.github.danielreker.t1homeworks.service1.model.Transaction;
import io.github.danielreker.t1homeworks.service1.model.dto.*;
import io.github.danielreker.t1homeworks.service1.model.enums.AccountStatus;
import io.github.danielreker.t1homeworks.service1.model.enums.ClientStatus;
import io.github.danielreker.t1homeworks.service1.model.enums.TransactionStatus;
import io.github.danielreker.t1homeworks.service1.repository.AccountRepository;
import io.github.danielreker.t1homeworks.service1.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    private static final long MAX_REJECTED_TRANSACTIONS = 5L;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private TransactionAcceptProducer transactionAcceptProducer;
    @Mock
    private ClientStatusService clientStatusService;

    @InjectMocks
    private TransactionService transactionService;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;
    @Captor
    private ArgumentCaptor<TransactionAcceptDto> transactionAcceptDtoCaptor;

    private Client testClient;
    private Account testAccount;
    private CreateTransactionRequest createDto;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(transactionService, "maxRejectedTransactions", MAX_REJECTED_TRANSACTIONS);

        testClient = new Client();
        testClient.setId(1L);
        testClient.setClientId(UUID.randomUUID());
        testClient.setStatus(ClientStatus.OPEN);

        testAccount = new Account();
        testAccount.setId(10L);
        testAccount.setAccountId(UUID.randomUUID());
        testAccount.setClient(testClient);
        testAccount.setStatus(AccountStatus.OPEN);
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setFrozenAmount(BigDecimal.ZERO);

        createDto = new CreateTransactionRequest(testAccount.getId(), new BigDecimal("-50.00"));
    }



    @Test
    void givenOpenAccount_whenCreate_thenTransactionIsRequested() {
        when(accountRepository.findById(createDto.accountId())).thenReturn(Optional.of(testAccount));
        when(transactionRepository.countByStatusEqualsAndAccount_Client_ClientIdEquals(
                TransactionStatus.REJECTED, testClient.getClientId())
        ).thenReturn(0L);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(transactionMapper.toTransactionDto(any(Transaction.class)))
                .thenAnswer(inv ->
                        new TransactionDto(null, null, null, null, null));


        transactionService.create(createDto);


        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();

        assertEquals(TransactionStatus.REQUESTED, savedTransaction.getStatus());
        assertEquals(testAccount, savedTransaction.getAccount());
        assertEquals(createDto.amount(), savedTransaction.getAmount());

        assertEquals(new BigDecimal("950.00"), testAccount.getBalance());

        verify(transactionAcceptProducer, times(1))
                .sendTransactionAccept(transactionAcceptDtoCaptor.capture());
        TransactionAcceptDto sentDto = transactionAcceptDtoCaptor.getValue();

        assertEquals(testClient.getClientId(), sentDto.clientId());
        assertEquals(testAccount.getAccountId(), sentDto.accountId());
        assertEquals(createDto.amount(), sentDto.transactionAmount());
    }

    @Test
    void givenClosedAccount_whenCreate_thenReturnsNull() {
        testAccount.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findById(createDto.accountId()))
                .thenReturn(Optional.of(testAccount));


        TransactionDto result = transactionService.create(createDto);


        assertNull(result);
        verify(transactionRepository, never()).save(any());
        verify(transactionAcceptProducer, never()).sendTransactionAccept(any());
    }

    @Test
    void givenUnknownClientStatusAndServiceReturnsBlocked_whenCreate_thenTransactionIsRejected() {
        testClient.setStatus(null);
        when(accountRepository.findById(createDto.accountId()))
                .thenReturn(Optional.of(testAccount));
        when(clientStatusService.getClientStatus(testClient.getClientId(), testAccount.getAccountId()))
                .thenReturn(new ClientStatusResponseDto(true));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(transactionMapper.toTransactionDto(any(Transaction.class)))
                .thenAnswer(inv ->
                        new TransactionDto(null, null, null, null, null));


        transactionService.create(createDto);


        assertEquals(ClientStatus.BLOCKED, testClient.getStatus());
        assertEquals(AccountStatus.BLOCKED, testAccount.getStatus());

        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(TransactionStatus.REJECTED, transactionCaptor.getValue().getStatus());

        assertEquals(new BigDecimal("1000.00"), testAccount.getBalance());

        verify(transactionAcceptProducer, never()).sendTransactionAccept(any());
    }

    @Test
    void givenRejectedLimitExceeded_whenCreate_thenAccountIsArrested() {
        when(accountRepository.findById(createDto.accountId()))
                .thenReturn(Optional.of(testAccount));
        when(transactionRepository.countByStatusEqualsAndAccount_Client_ClientIdEquals(
                TransactionStatus.REJECTED, testClient.getClientId())
        )
                .thenReturn(MAX_REJECTED_TRANSACTIONS + 1);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(transactionMapper.toTransactionDto(any(Transaction.class)))
                .thenAnswer(inv ->
                        new TransactionDto(null, null, null, null, null));


        transactionService.create(createDto);


        assertEquals(AccountStatus.ARRESTED, testAccount.getStatus());

        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(TransactionStatus.REJECTED, transactionCaptor.getValue().getStatus());

        verify(transactionAcceptProducer, never()).sendTransactionAccept(any());
    }

    @Test
    void givenInvalidAccountId_whenCreate_thenThrowsNotFoundException() {
        when(accountRepository.findById(createDto.accountId())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> transactionService.create(createDto));
    }



    @Test
    void givenBlockedResult_whenProcessResult_thenReversesBalanceAndBlocksAccount() {
        Transaction existingTransaction = Transaction.builder()
                .transactionId(UUID.randomUUID())
                .account(testAccount)
                .amount(new BigDecimal("-50.00"))
                .status(TransactionStatus.REQUESTED)
                .build();

        TransactionResultDto resultDto = new TransactionResultDto(
                TransactionStatus.BLOCKED, testAccount.getAccountId(), existingTransaction.getTransactionId()
        );

        when(transactionRepository.findByTransactionId(resultDto.transactionId())).thenReturn(existingTransaction);
        when(accountRepository.findByAccountId(resultDto.accountId())).thenReturn(testAccount);


        transactionService.processResult(resultDto);


        assertEquals(TransactionStatus.BLOCKED, existingTransaction.getStatus());

        assertEquals(new BigDecimal("1050.00"), testAccount.getBalance());
        assertEquals(new BigDecimal("-50.00"), testAccount.getFrozenAmount());
        assertEquals(AccountStatus.BLOCKED, testAccount.getStatus());
    }

    @Test
    void givenRejectedResult_whenProcessResult_thenReversesBalance() {
        Transaction existingTransaction = Transaction.builder()
                .transactionId(UUID.randomUUID())
                .account(testAccount)
                .amount(new BigDecimal("-50.00"))
                .status(TransactionStatus.REQUESTED)
                .build();
        TransactionResultDto resultDto = new TransactionResultDto(
                TransactionStatus.REJECTED, testAccount.getAccountId(), existingTransaction.getTransactionId()
        );

        when(transactionRepository.findByTransactionId(resultDto.transactionId())).thenReturn(existingTransaction);
        when(accountRepository.findByAccountId(resultDto.accountId())).thenReturn(testAccount);


        transactionService.processResult(resultDto);


        assertEquals(TransactionStatus.REJECTED, existingTransaction.getStatus());

        assertEquals(new BigDecimal("1050.00"), testAccount.getBalance());
        assertEquals(BigDecimal.ZERO, testAccount.getFrozenAmount());
        assertEquals(AccountStatus.OPEN, testAccount.getStatus());
    }

    @Test
    void givenAcceptedResult_whenProcessResult_thenUpdatesStatusOnly() {
        Transaction existingTransaction = Transaction.builder()
                .transactionId(UUID.randomUUID())
                .account(testAccount)
                .amount(new BigDecimal("-50.00"))
                .status(TransactionStatus.REQUESTED)
                .build();
        TransactionResultDto resultDto = new TransactionResultDto(
                TransactionStatus.ACCEPTED, testAccount.getAccountId(), existingTransaction.getTransactionId()
        );

        when(transactionRepository.findByTransactionId(resultDto.transactionId())).thenReturn(existingTransaction);


        transactionService.processResult(resultDto);


        assertEquals(TransactionStatus.ACCEPTED, existingTransaction.getStatus());

        verify(accountRepository, never()).findByAccountId(any());
        assertEquals(new BigDecimal("1000.00"), testAccount.getBalance());
    }

    @Test
    void givenNonExistentTransactionId_whenProcessResult_thenDoesNothing() {
        TransactionResultDto resultDto = new TransactionResultDto(
                TransactionStatus.BLOCKED, UUID.randomUUID(), UUID.randomUUID()
        );
        when(transactionRepository.findByTransactionId(resultDto.transactionId())).thenReturn(null);


        transactionService.processResult(resultDto);


        verify(accountRepository, never()).findByAccountId(any());
        verifyNoMoreInteractions(accountRepository, transactionMapper, transactionAcceptProducer);
    }
}