package br.com.tassisf.bank.business.impl;

import br.com.tassisf.bank.business.AccountService;
import br.com.tassisf.bank.business.CustomerService;
import br.com.tassisf.bank.controller.in.AccountRequest;
import br.com.tassisf.bank.controller.out.AccountResponse;
import br.com.tassisf.bank.domain.entity.Account;
import br.com.tassisf.bank.domain.entity.Customer;
import br.com.tassisf.bank.domain.mapper.AccountMapper;
import br.com.tassisf.bank.exception.BusinessException;
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
        log.info("Criando nova conta para o cliente: {}", accountRequest.customerName());

        String accountNumber = generateAccountNumber(accountRequest);
        validateAccountNumberNotExists(accountNumber);

        Customer customer = findCustomerById(accountRequest.customerId());
        Account accountEntity = Account.builder()
                .balance(BigDecimal.ZERO)
                .creditLimit(BigDecimal.ZERO)
                .accountNumber(accountNumber)
                .customer(customer)
                .build();

        return accountMapper.toResponse(repository.save(accountEntity));
    }

    @Override
    public List<AccountResponse> findAccountsByCustomer(UUID customerId) {
        log.info("Buscando contas do cliente com ID: {}", customerId);
        Customer customer = findCustomerById(customerId);
        return accountMapper.toResponseList(repository.findByCustomer(customer));
    }

    @Override
    public AccountResponse updateCreditLimit(String accountNumber, BigDecimal newCreditLimit) {
        log.info("Atualizando limite de crédito da conta: {}", accountNumber);
        Account account = repository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada"));

        if (newCreditLimit.compareTo(account.getCreditLimit()) < 0) {
            throw new BusinessException("Novo limite de crédito não pode ser menor que o atual");
        }

        account.setCreditLimit(newCreditLimit);
        Account updatedAccount = repository.save(account);
        return accountMapper.toResponse(updatedAccount);
    }

    private Customer findCustomerById(UUID customerId) {
        return customerService.findCustomerEntityById(customerId).orElseThrow(() ->
                new ResourceNotFoundException("Cliente não encontrado"));
    }

    private void validateAccountNumberNotExists(String accountNumber) {
        if (repository.findByAccountNumber(accountNumber).isPresent()) {
            throw new ResourceAlreadyExistsException("Conta já cadastrada");
        }
    }

    private String generateAccountNumber(AccountRequest accountRequest) {
        String initials = accountRequest.customerName()
                .replaceAll("[^A-Za-z]", "")
                .toUpperCase()
                .substring(0, Math.min(2, accountRequest.customerName().length()));
        int randomNumber = (int) (Math.random() * 1_000_000);
        String number = String.format("%06d", randomNumber);
        return initials + "-" + number;
    }
}
