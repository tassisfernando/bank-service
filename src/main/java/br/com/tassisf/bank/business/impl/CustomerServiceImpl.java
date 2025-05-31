package br.com.tassisf.bank.business.impl;

import br.com.tassisf.bank.business.CustomerService;
import br.com.tassisf.bank.controller.in.CustomerRequest;
import br.com.tassisf.bank.controller.out.CustomerResponse;
import br.com.tassisf.bank.domain.entity.Customer;
import br.com.tassisf.bank.domain.mapper.CustomerMapper;
import br.com.tassisf.bank.exception.ResourceAlreadyExistsException;
import br.com.tassisf.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CustomerResponse createCustomer(CustomerRequest customer) {
        if (repository.findByCpf(customer.cpf()).isPresent()) {
            throw new ResourceAlreadyExistsException("CPF já cadastrado");
        }

        log.info("Criando novo cliente com CPF: {}", customer.cpf());
        Customer customerEntity = buildCustomerEntity(customer);
        return customerMapper.toResponse(repository.save(customerEntity));
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        log.info("Buscando cliente com ID: {}", id);
        return repository.findById(id);
    }

    @Override
    public List<CustomerResponse> findAll() {
        log.info("Buscando todos os clientes");
        List<Customer> customers = repository.findAll();
        return customerMapper.toResponseList(customers);
    }

    private Customer buildCustomerEntity(CustomerRequest customer) {
        String hashedPassword = passwordEncoder.encode(customer.password());
        Customer customerEntity = customerMapper.toEntity(customer);
        customerEntity.setPassword(hashedPassword);

        return customerEntity;
    }
}
