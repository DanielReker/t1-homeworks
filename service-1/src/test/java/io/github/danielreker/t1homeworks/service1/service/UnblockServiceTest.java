package io.github.danielreker.t1homeworks.service1.service;

import io.github.danielreker.t1homeworks.service1.model.Account;
import io.github.danielreker.t1homeworks.service1.model.Client;
import io.github.danielreker.t1homeworks.service1.model.dto.UnblockAccountRequest;
import io.github.danielreker.t1homeworks.service1.model.dto.UnblockAccountResponse;
import io.github.danielreker.t1homeworks.service1.model.dto.UnblockClientRequest;
import io.github.danielreker.t1homeworks.service1.model.dto.UnblockClientResponse;
import io.github.danielreker.t1homeworks.service1.model.enums.AccountStatus;
import io.github.danielreker.t1homeworks.service1.model.enums.ClientStatus;
import io.github.danielreker.t1homeworks.service1.repository.AccountRepository;
import io.github.danielreker.t1homeworks.service1.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnblockServiceTest {

    private static final String FAKE_SERVICE_3_URL = "http://localhost:8083/api/v1";
    private static final int ENTITIES_PER_REQUEST = 10;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.RequestBodySpec requestBodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private UnblockService unblockService;

    @Captor
    private ArgumentCaptor<UnblockClientRequest> clientRequestCaptor;
    @Captor
    private ArgumentCaptor<UnblockAccountRequest> accountRequestCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(unblockService, "service3Url", FAKE_SERVICE_3_URL);
        ReflectionTestUtils.setField(unblockService, "clientsPerRequest", ENTITIES_PER_REQUEST);
        ReflectionTestUtils.setField(unblockService, "accountsPerRequest", (long) ENTITIES_PER_REQUEST);
    }



    private void mockRestClientChain(Class<?> responseType, Object responseBody) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(java.net.URI.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(eq(responseType))).thenAnswer(inv -> responseBody);
    }



    @Test
    @DisplayName("unblockClients: Should fetch, send, and process unblocked clients successfully")
    void unblockClients_happyPath() {
        Client client1 = mock(Client.class);
        when(client1.getClientId()).thenReturn(UUID.randomUUID());
        Client client2 = mock(Client.class);
        when(client2.getClientId()).thenReturn(UUID.randomUUID());
        List<Client> blockedClients = List.of(client1, client2);

        when(clientRepository.findByStatusIn(List.of(ClientStatus.BLOCKED), Limit.of(ENTITIES_PER_REQUEST)))
                .thenReturn(blockedClients);

        UnblockClientResponse response = new UnblockClientResponse(List.of(client1.getClientId()));
        mockRestClientChain(UnblockClientResponse.class, response);

        when(clientRepository.findByClientId(client1.getClientId())).thenReturn(client1);


        unblockService.unblockClients();


        verify(requestBodySpec).body(clientRequestCaptor.capture());
        UnblockClientRequest sentRequest = clientRequestCaptor.getValue();
        assertEquals(2, sentRequest.clientIds().size());
        assertTrue(sentRequest.clientIds().containsAll(List.of(client1.getClientId(), client2.getClientId())));

        verify(clientRepository).findByClientId(client1.getClientId());
        verify(client1).setStatus(ClientStatus.OPEN);

        verify(clientRepository, never()).findByClientId(client2.getClientId());
        verify(client2, never()).setStatus(any());
    }

    @Test
    void unblockClients_whenNoBlockedClientsFound() {
        when(clientRepository.findByStatusIn(anyList(), any(Limit.class))).thenReturn(List.of());


        unblockService.unblockClients();


        verify(restClient, never()).post();
    }



    @Test
    void unblockAccounts_happyPath() {
        Account account1 = mock(Account.class);
        when(account1.getAccountId()).thenReturn(UUID.randomUUID());
        Account account2 = mock(Account.class);
        when(account2.getAccountId()).thenReturn(UUID.randomUUID());
        List<Account> blockedAccounts = List.of(account1, account2);

        List<AccountStatus> statuses = List.of(AccountStatus.BLOCKED, AccountStatus.ARRESTED);
        when(accountRepository.findByStatusIn(statuses, Limit.of(ENTITIES_PER_REQUEST)))
                .thenReturn(blockedAccounts);

        UnblockAccountResponse response = new UnblockAccountResponse(List.of(account1.getAccountId()));
        mockRestClientChain(UnblockAccountResponse.class, response);

        when(accountRepository.findByAccountId(account1.getAccountId())).thenReturn(account1);


        unblockService.unblockAccounts();


        verify(requestBodySpec).body(accountRequestCaptor.capture());
        UnblockAccountRequest sentRequest = accountRequestCaptor.getValue();
        assertEquals(2, sentRequest.accountIds().size());
        assertTrue(sentRequest.accountIds().containsAll(List.of(account1.getAccountId(), account2.getAccountId())));

        verify(accountRepository).findByAccountId(account1.getAccountId());
        verify(account1).setStatus(AccountStatus.OPEN);

        verify(accountRepository, never()).findByAccountId(account2.getAccountId());
        verify(account2, never()).setStatus(any());
    }

    @Test
    void unblockAccounts_whenNoBlockedAccountsFound() {
        when(accountRepository.findByStatusIn(anyList(), any(Limit.class))).thenReturn(List.of());


        unblockService.unblockAccounts();


        verify(restClient, never()).post();
    }
}