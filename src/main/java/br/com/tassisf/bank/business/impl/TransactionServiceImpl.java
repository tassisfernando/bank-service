package br.com.tassisf.bank.business.impl;

import br.com.tassisf.bank.business.TransactionService;
import br.com.tassisf.bank.controller.in.TransactionRequest;
import br.com.tassisf.bank.controller.out.StatementResponse;
import br.com.tassisf.bank.controller.out.TransactionResponse;
import br.com.tassisf.bank.domain.entity.Account;
import br.com.tassisf.bank.domain.entity.Transaction;
import br.com.tassisf.bank.domain.enums.Operation;
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
    public TransactionResponse process(TransactionRequest request, Operation operation, UUID customerId) {
        log.info("Processing transaction type {} for account {}", operation, request.getAccountNumberOrigin());

        var originAccount = findAccountByNumberAndCustomer(request.getAccountNumberOrigin(), customerId);

        validateTransaction(originAccount, request, operation);

        if (operation.equals(Operation.DEPOSIT)) {
            validateDepositBonus(request, customerId, originAccount);
        }

        var transaction = createTransaction(originAccount, null, request.getAmount(), operation);
        updateAccountBalance(originAccount, request.getAmount(), operation);
        saveData(transaction, originAccount);

        return createTransactionResponse(transaction, originAccount.getBalance());
    }

    @Override
    public TransactionResponse processTransfer(TransactionRequest request, UUID customerId) {
        log.info("Processing transfer from account {} to {}", request.getAccountNumberOrigin(),
                request.getAccountNumberDestin());

        if (request.getAccountNumberOrigin().equals(request.getAccountNumberDestin())) {
            log.error("Transfer between the same account is not allowed: {}", request.getAccountNumberOrigin());
            throw new BusinessException("Cannot transfer to the same account");
        }

        var originAccount = findAccountByNumberAndCustomer(request.getAccountNumberOrigin(), customerId);
        var destinationAccount = findAccountByNumber(request.getAccountNumberDestin());

        validateTransaction(originAccount, request, Operation.WITHDRAW);

        // Transferência entre contas de diferentes clientes gera taxa
        if (!originAccount.getCustomer().getId().equals(destinationAccount.getCustomer().getId())) {
            var feeAmount = request.getAmount().multiply(BigDecimal.valueOf(0.1));
            var transactionFee = createTransaction(originAccount, null, feeAmount, Operation.FEE);

            updateAccountBalance(originAccount, feeAmount, Operation.FEE);
            transactionRepository.save(transactionFee);
        }

        updateAccountBalance(originAccount, request.getAmount(), Operation.WITHDRAW);
        updateAccountBalance(destinationAccount, request.getAmount(), Operation.DEPOSIT);
        var transaction = createTransaction(originAccount, destinationAccount, request.getAmount(), Operation.TRANSFER);

        // Atualiza os saldos das contas
        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);

        // Salva a transação
        transactionRepository.save(transaction);

        return createTransactionResponse(transaction, originAccount.getBalance());
    }

    @Override
    @Cacheable(value = "accountStatements", key = "#accountId + '-' + #customerId")
    public List<StatementResponse> getAccountStatement(String accountId, UUID customerId) {
        log.info("Retrieving account statement for account {} and customer {}", accountId, customerId);
        Account account = findAccountByNumberAndCustomer(accountId, customerId);

        List<Transaction> transactions = transactionRepository.findByOriginAccount(account);
        List<Transaction> transactionsDestin = transactionRepository.findByDestinationAccount(account);

        transactions.addAll(transactionsDestin);

        return transactionMapper.toResponseList(transactions);
    }

    private Account findAccountByNumberAndCustomer(String accountNumber, UUID customerId) {
        return accountRepository.findByAccountNumberAndCustomer_Id(accountNumber, customerId)
                .orElseThrow(() -> {
                    log.error("Account {} not found for customer {}", accountNumber, customerId);
                    return new ResourceNotFoundException("Account not found");
                });
    }

    private Account findAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found: {}", accountNumber);
                    return new ResourceNotFoundException("Account not found");
                });
    }

    private void validateTransaction(Account account, TransactionRequest request, Operation operation) {
        BigDecimal totalBalance = account.getBalance().add(account.getCreditLimit());
        List<Operation> allowedOperations = Operation.getOperationsByType(TransactionType.DEBIT);

        if (allowedOperations.contains(operation) || Operation.TRANSFER.equals(operation)) {
            if (totalBalance.compareTo(request.getAmount()) < 0) {
                log.error("Insufficient funds for {} in account {}", operation.getDescription(),
                        request.getAccountNumberOrigin());
                throw new BusinessException(String.format("Insufficient funds for %s", operation.getDescription()));
            }
        }
    }

    private Transaction createTransaction(Account originAccount, Account destinAccount, BigDecimal amount,
                                          Operation operation) {
        return Transaction.builder()
                .amount(amount)
                .type(operation.getTransactionType())
                .operation(operation)
                .dateTime(LocalDateTime.now())
                .originAccount(originAccount)
                .destinationAccount(destinAccount)
                .build();
    }

    private void updateAccountBalance(Account account, BigDecimal amount, Operation operation) {
        if (TransactionType.CREDIT.equals(operation.getTransactionType())) {
            account.setBalance(account.getBalance().add(amount));
        } else {
            account.setBalance(account.getBalance().subtract(amount));
        }
        log.debug("Account balance for {} updated to {}", account.getAccountNumber(), account.getBalance());
    }

    private void saveData(Transaction transaction, Account account) {
        transactionRepository.save(transaction);
        accountRepository.save(account);
    }

    private TransactionResponse createTransactionResponse(Transaction transaction, BigDecimal newBalance) {
        String description = transaction.getOperation().getDescription();
        log.info("Transaction {} completed successfully", description);
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType().name(),
                newBalance,
                description + " realizado com sucesso."
        );
    }

    private void validateDepositBonus(TransactionRequest request, UUID customerId, Account originAccount) {
        BigDecimal totalAccountsBalance = accountRepository.getTotalBalanceByCustomerId(customerId);

        if (isEligibleForBonus(request.getAmount(), totalAccountsBalance)) {
            BigDecimal bonusAmount = calculateBonusAmount(request.getAmount());
            request.setAmount(request.getAmount().add(bonusAmount));

            var bonusTransaction = createTransaction(originAccount, null, bonusAmount, Operation.BONUS);
            transactionRepository.save(bonusTransaction);
            log.info("Applying bonus of {} to account {}", bonusAmount, originAccount.getAccountNumber());
        }
    }

    private boolean isEligibleForBonus(BigDecimal depositAmount, BigDecimal totalAccountsBalance) {
        return depositAmount.compareTo(totalAccountsBalance) > 0;
    }
    
    private BigDecimal calculateBonusAmount(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(0.1));
    }
}