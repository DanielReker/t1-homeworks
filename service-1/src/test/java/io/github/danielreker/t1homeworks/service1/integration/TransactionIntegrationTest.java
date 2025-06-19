package io.github.danielreker.t1homeworks.service1.integration;

import io.github.danielreker.t1homeworks.service1.kafka.TransactionAcceptProducer;
import io.github.danielreker.t1homeworks.service1.model.Account;
import io.github.danielreker.t1homeworks.service1.model.Client;
import io.github.danielreker.t1homeworks.service1.model.Transaction;
import io.github.danielreker.t1homeworks.service1.model.dto.CreateTransactionRequest;
import io.github.danielreker.t1homeworks.service1.model.dto.TransactionAcceptDto;
import io.github.danielreker.t1homeworks.service1.model.enums.AccountStatus;
import io.github.danielreker.t1homeworks.service1.model.enums.AccountType;
import io.github.danielreker.t1homeworks.service1.model.enums.ClientStatus;
import io.github.danielreker.t1homeworks.service1.model.enums.TransactionStatus;
import io.github.danielreker.t1homeworks.service1.repository.AccountRepository;
import io.github.danielreker.t1homeworks.service1.repository.ClientRepository;
import io.github.danielreker.t1homeworks.service1.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @MockitoBean
    private TransactionAcceptProducer mockTransactionAcceptProducer;

    @Captor
    private ArgumentCaptor<TransactionAcceptDto> transactionAcceptDtoCaptor;

    private Client testClient;
    private Account testAccount;

    @BeforeEach
    void setUpDatabase() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        clientRepository.deleteAll();

        testClient = new Client();
        testClient.setClientId(UUID.randomUUID());
        testClient.setFirstName("John");
        testClient.setLastName("Doe");
        testClient.setStatus(ClientStatus.OPEN);
        clientRepository.save(testClient);

        testAccount = new Account();
        testAccount.setAccountId(UUID.randomUUID());
        testAccount.setClient(testClient);
        testAccount.setStatus(AccountStatus.OPEN);
        testAccount.setType(AccountType.CREDIT);
        testAccount.setBalance(new BigDecimal("1000.00"));
        accountRepository.save(testAccount);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTransaction_withMockedProducer_success() throws Exception {
        CreateTransactionRequest request =
                new CreateTransactionRequest(testAccount.getId(), new BigDecimal("-50.00"));


        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REQUESTED"))
                .andExpect(jsonPath("$.amount").value(-50.00));

        Account updatedAccount = accountRepository.findById(testAccount.getId()).orElseThrow();
        assertThat(updatedAccount.getBalance()).isEqualByComparingTo("950.00");

        Transaction savedTransaction = transactionRepository.findAll().getFirst();
        assertThat(savedTransaction.getStatus()).isEqualTo(TransactionStatus.REQUESTED);
        assertThat(savedTransaction.getAccount().getId()).isEqualTo(testAccount.getId());

        verify(mockTransactionAcceptProducer).sendTransactionAccept(transactionAcceptDtoCaptor.capture());

        TransactionAcceptDto sentDto = transactionAcceptDtoCaptor.getValue();
        assertThat(sentDto.accountId()).isEqualTo(testAccount.getAccountId());
        assertThat(sentDto.transactionAmount()).isEqualByComparingTo("-50.00");
        assertThat(sentDto.accountBalance()).isEqualByComparingTo("950.00");
    }
}