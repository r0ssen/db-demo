package dk.rossen.dbdemo.controller;

import dk.rossen.dbdemo.model.CustomerResponse;
import dk.rossen.dbdemo.model.NewCustomerRequest;
import dk.rossen.dbdemo.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@RequestBody @Valid NewCustomerRequest newCustomerRequest) {
       return customerService.createCustomer(newCustomerRequest);
    }

    @GetMapping(path = "/{customer-number}")
    public CustomerResponse getCustomerById(@PathVariable(name = "customer-number") String customerNumber) {
        return customerService.getCustomerByCustomerNumber(customerNumber);
    }
}
