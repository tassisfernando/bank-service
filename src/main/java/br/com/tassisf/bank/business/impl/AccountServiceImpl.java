package br.com.tassisf.bank.business.impl;

import br.com.tassisf.bank.business.AccountService;
import br.com.tassisf.bank.business.CustomerService;
import br.com.tassisf.bank.controller.in.AccountRequest;
import br.com.tassisf.bank.controller.out.AccountResponse;
import br.com.tassisf.bank.domain.entity.Account;
import br.com.tassisf.bank.domain.entity.Customer;
import br.com.tassisf.bank.domain.mapper.AccountMapper;
import br.com.tassisf.bank.exception.ResourceAlreadyExistsException;
import br.com.tassisf.bank.exception.ResourceNotFoundException;
import br.com.tassisf.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final AccountMapper accountMapper;
    private final CustomerService customerService;

    @Override
    public AccountResponse createAccount(AccountRequest accountRequest) {
        Customer customer = findCustomerById(accountRequest.customerId());
        validateAccountNumberNotExists(accountRequest.accountNumber());
        log.info("Criando nova conta para o cliente: {}", customer.getName());

        Account accountEntity = accountMapper.toEntity(accountRequest);
        accountEntity.setCustomer(customer);
        accountEntity.setBalance(BigDecimal.ZERO);
        return accountMapper.toResponse(repository.save(accountEntity));
    }

    @Override
    public List<AccountResponse> findAccountsByCustomer(UUID customerId) {
        log.info("Buscando contas do cliente com ID: {}", customerId);
        Customer customer = findCustomerById(customerId);
        return accountMapper.toResponseList(repository.findByCustomer(customer));
    }

    private Customer findCustomerById(UUID customerId) {
        return customerService.findById(customerId).orElseThrow(() ->
                new ResourceNotFoundException("Cliente não encontrado"));
    }

    private void validateAccountNumberNotExists(String accountNumber) {
        if (repository.findByAccountNumber(accountNumber).isPresent()) {
            throw new ResourceAlreadyExistsException("Conta já cadastrada");
        }
    }
}
