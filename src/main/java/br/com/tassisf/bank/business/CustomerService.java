package br.com.tassisf.bank.business;

import br.com.tassisf.bank.controller.in.CustomerRequest;
import br.com.tassisf.bank.controller.out.CustomerResponse;
import br.com.tassisf.bank.domain.entity.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest customer);

    Optional<Customer> findCustomerEntityById(UUID id);

    CustomerResponse findById(UUID id);

    List<CustomerResponse> findAll();
}
