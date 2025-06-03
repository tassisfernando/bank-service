package br.com.tassisf.bank.business.impl;

import br.com.tassisf.bank.business.TransactionService;
import br.com.tassisf.bank.controller.in.TransactionRequest;
import br.com.tassisf.bank.controller.out.StatementResponse;
import br.com.tassisf.bank.controller.out.TransactionResponse;
import br.com.tassisf.bank.domain.entity.Account;
import br.com.tassisf.bank.domain.entity.Transaction;
import br.com.tassisf.bank.domain.enums.TransactionType;
import br.com.tassisf.bank.domain.mapper.TransactionMapper;
import br.com.tassisf.bank.exception.BusinessException;
import br.com.tassisf.bank.exception.ResourceNotFoundException;
import br.com.tassisf.bank.repository.AccountRepository;
import br.com.tassisf.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionResponse process(TransactionRequest request, TransactionType transactionType, UUID customerId) {
        log.info("Processing transaction type {} for account {}", transactionType, request.accountNumber());

        Account account = findAndValidateAccount(request.accountNumber(), customerId);
        validateTransaction(account, request, transactionType);

        Transaction transaction = createTransaction(account, request, transactionType);
        updateAccountBalance(account, request, transactionType);
        saveData(transaction, account);

        return createTransactionResponse(transaction, account.getBalance());
    }

    @Override
    @Cacheable(value = "accountStatements", key = "#accountId + '-' + #customerId")
    public List<StatementResponse> getAccountStatement(String accountId, UUID customerId) {
        log.info("Retrieving account statement for account {} and customer {}", accountId, customerId);
        Account account = findAndValidateAccount(accountId, customerId);

        List<Transaction> transactions = transactionRepository.findByAccount(account);

        return transactionMapper.toResponseList(transactions);
    }

    private Account findAndValidateAccount(String accountNumber, UUID customerId) {
        return accountRepository.findByAccountNumberAndCustomer_Id(accountNumber, customerId)
                .orElseThrow(() -> {
                    log.error("Account not found: {}", accountNumber);
                    return new ResourceNotFoundException("Account not found");
                });
    }

    private void validateTransaction(Account account, TransactionRequest request, TransactionType transactionType) {
        if (TransactionType.DEBIT.equals(transactionType) &&
                account.getBalance().compareTo(request.amount()) < 0) {
            log.error("Insufficient funds for withdrawal in account {}", request.accountNumber());
            throw new BusinessException("Insufficient funds for withdrawal");
        }
    }

    private Transaction createTransaction(Account account, TransactionRequest request, TransactionType transactionType) {
        return Transaction.builder()
                .amount(request.amount())
                .type(transactionType)
                .dateTime(LocalDateTime.now())
                .account(account)
                .build();
    }

    private void updateAccountBalance(Account account, TransactionRequest request, TransactionType transactionType) {
        if (TransactionType.CREDIT.equals(transactionType)) {
            account.setBalance(account.getBalance().add(request.amount()));
        } else {
            account.setBalance(account.getBalance().subtract(request.amount()));
        }
        log.debug("Account balance for {} updated to {}", account.getAccountNumber(), account.getBalance());
    }

    private void saveData(Transaction transaction, Account account) {
        transactionRepository.save(transaction);
        accountRepository.save(account);
    }

    private TransactionResponse createTransactionResponse(Transaction transaction, BigDecimal newBalance) {
        String description = transaction.getType().getDescription();
        log.info("Transaction {} completed successfully", description);
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType().name(),
                newBalance,
                description + " realizado com sucesso."
        );
    }
}