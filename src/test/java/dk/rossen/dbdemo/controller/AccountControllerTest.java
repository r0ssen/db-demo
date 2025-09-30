package dk.rossen.dbdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.rossen.dbdemo.model.*;
import dk.rossen.dbdemo.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public AccountService accountService() {
            return Mockito.mock(AccountService.class);
        }
    }
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAccount() throws Exception {
        NewAccountRequest request = new NewAccountRequest("1234-123456", "New account");
        AccountResponse response = new AccountResponse(1L, "ACC123", "New account", BigDecimal.ZERO);

        Mockito.when(accountService.createAccount(any())).thenReturn(response);

        mockMvc.perform(post("/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC123"))
                .andExpect(jsonPath("$.accountName").value("New account"));
    }

    @Test
    void testGetAccount() throws Exception {
        AccountResponse response = new AccountResponse(1L,"ACC123", "New Account", BigDecimal.valueOf(1000));

        Mockito.when(accountService.getAccountByAccountNumber("ACC123")).thenReturn(response);

        mockMvc.perform(get("/v1/accounts/ACC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC123"))
                .andExpect(jsonPath("$.accountName").value("New Account"));
    }

    @Test
    void testDeposit() throws Exception {
        AccountDepositRequest depositRequest = new AccountDepositRequest("ACC123", BigDecimal.valueOf(200));

        mockMvc.perform(put("/v1/accounts/ACC123/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isNoContent());

        Mockito.verify(accountService).deposit(any(AccountDepositRequest.class));
    }

    @Test
    void testDepositPathMismatch() throws Exception {
        AccountDepositRequest depositRequest = new AccountDepositRequest("ACC321", BigDecimal.valueOf(200));

        mockMvc.perform(put("/v1/accounts/ACC123/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithdraw() throws Exception {
        AccountWithdrawRequest withdrawRequest = new AccountWithdrawRequest("ACC123","ACC321", BigDecimal.valueOf(150), "Some text");

        mockMvc.perform(put("/v1/accounts/ACC123/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isNoContent());

        Mockito.verify(accountService).withdraw(any(AccountWithdrawRequest.class));
    }

    @Test
    void testWithdrawPathMismatch() throws Exception {
        AccountWithdrawRequest withdrawRequest = new AccountWithdrawRequest("ACC321", "ACC556", BigDecimal.valueOf(150), "Transaction");

        mockMvc.perform(put("/v1/accounts/ACC123/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPostings() throws Exception {
        List<AccountPosting> postings = List.of(
                new AccountPosting(LocalDateTime.now(), "ACC123", "ACC321", BigDecimal.valueOf(500), "Text", BigDecimal.valueOf(400)),
                new AccountPosting(LocalDateTime.now(), "ACC123", "ACC321", BigDecimal.valueOf(500), "Text", BigDecimal.valueOf(400))
        );

        Mockito.when(accountService.getPostings("ACC123")).thenReturn(postings);

        mockMvc.perform(get("/v1/accounts/ACC123/postings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
