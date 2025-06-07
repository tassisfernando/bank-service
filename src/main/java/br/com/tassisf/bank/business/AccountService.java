package br.com.tassisf.bank.business;

import br.com.tassisf.bank.controller.in.AccountRequest;
import br.com.tassisf.bank.controller.out.AccountResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountResponse createAccount(AccountRequest accountRequest);

    List<AccountResponse> findAccountsByCustomer(UUID customerId);

    AccountResponse updateCreditLimit(String accountNumber, BigDecimal newCreditLimit);

}
