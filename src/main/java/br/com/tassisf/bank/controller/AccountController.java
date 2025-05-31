package br.com.tassisf.bank.controller;

import br.com.tassisf.bank.business.AccountService;
import br.com.tassisf.bank.business.CustomerService;
import br.com.tassisf.bank.controller.in.AccountRequest;
import br.com.tassisf.bank.controller.in.CustomerRequest;
import br.com.tassisf.bank.controller.out.AccountResponse;
import br.com.tassisf.bank.controller.out.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest accountRequest) {
        AccountResponse accountResponse = accountService.createAccount(accountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponse);
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<List<AccountResponse>> findAccountsByCustomer(@PathVariable UUID customerId) {
        List<AccountResponse> accountResponseList = accountService.findAccountsByCustomer(customerId);
        return ResponseEntity.ok(accountResponseList);
    }

}
