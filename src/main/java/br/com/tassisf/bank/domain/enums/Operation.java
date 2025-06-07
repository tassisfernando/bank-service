package br.com.tassisf.bank.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Operation {
    WITHDRAW("Saque", TransactionType.DEBIT),
    DEPOSIT("Depósito", TransactionType.CREDIT),
    TRANSFER("Transferência", TransactionType.DEPENDS),
    BONUS("Bônus", TransactionType.CREDIT),
    FEE("Taxa", TransactionType.DEBIT);

    public static List<Operation> getOperationsByType(TransactionType type) {
        return Stream.of(values())
                .filter(operation -> operation.getTransactionType() == type)
                .toList();
    }

    private final String description;
    private final TransactionType transactionType;
}
