package dk.rossen.dbdemo.service;

import dk.rossen.dbdemo.exception.CustomerAlreadyExistsException;
import dk.rossen.dbdemo.exception.CustomerNotFoundException;
import dk.rossen.dbdemo.model.AccountResponse;
import dk.rossen.dbdemo.model.CustomerResponse;
import dk.rossen.dbdemo.model.NewCustomerRequest;
import dk.rossen.dbdemo.repository.CustomerRepository;
import dk.rossen.dbdemo.repository.entity.Customer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerResponse createCustomer(@Valid NewCustomerRequest newCustomerRequest) {
        customerNumberExists(newCustomerRequest.customerNumber());

        Customer customerEntity = customerRepository.save(
                Customer.builder()
                        .firstName(newCustomerRequest.firstName())
                        .lastName(newCustomerRequest.lastName())
                        .customerNumber(newCustomerRequest.customerNumber())
                        .build()
        );
        return new CustomerResponse(customerEntity.getId(), customerEntity.getCustomerNumber(), customerEntity.getFirstName(), customerEntity.getLastName(), null);
    }

    public CustomerResponse getCustomerByCustomerNumber(String customerNumber) {
        Customer customer = getCustomer(customerNumber);

        List<AccountResponse> accountResponseList = null;
        if (!customer.getAccounts().isEmpty()) {
            accountResponseList = customer.getAccounts().stream().map(a ->
                    new AccountResponse(
                            a.getId(),
                            a.getAccountNumber(),
                            a.getAccountName(),
                            a.getBalance()
                    ))
                    .toList();
        }

        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerNumber(),
                customer.getFirstName(),
                customer.getLastName(),
                accountResponseList
        );
    }

    private void customerNumberExists(String customerNumber) {
       if (customerRepository.findByCustomerNumber(customerNumber).isPresent()) {
           throw new CustomerAlreadyExistsException();
       }
    }

    public Customer getCustomer(String customerNumber) {
        return customerRepository.findByCustomerNumber(customerNumber).orElseThrow(CustomerNotFoundException::new);
    }
}
