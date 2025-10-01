package dk.rossen.dbdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.rossen.dbdemo.model.CustomerResponse;
import dk.rossen.dbdemo.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CustomerController.class})
class CustomerControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockitoBean
    CustomerService customerService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getCustomerById() throws Exception {
        CustomerResponse customerResponse = new CustomerResponse("1234", "Anders", "And", Collections.emptyList(), LocalDateTime.now());

        when(customerService.getCustomerByCustomerNumber(anyString())).thenReturn(customerResponse);

        mockMvc.perform(
                        get("/v1/customers/{customer-id}", customerResponse.customerNumber()))
                .andExpect(status().isOk()
                ).andExpect(content().json(objectMapper.writeValueAsString(customerResponse)));

        verify(customerService).getCustomerByCustomerNumber(anyString());
    }
}