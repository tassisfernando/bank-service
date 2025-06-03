package br.com.tassisf.bank.business;

import br.com.tassisf.bank.controller.in.TransactionRequest;
import br.com.tassisf.bank.controller.out.StatementResponse;
import br.com.tassisf.bank.controller.out.TransactionResponse;
import br.com.tassisf.bank.domain.enums.TransactionType;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionResponse process(TransactionRequest request, TransactionType transactionType, UUID customerId);

    List<StatementResponse> getAccountStatement(String accountId, UUID customerId);
}
