package br.com.tassisf.bank.domain.mapper;

import br.com.tassisf.bank.controller.out.StatementResponse;
import br.com.tassisf.bank.domain.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(target = "accountNumberOrigin", source = "originAccount.accountNumber")
    @Mapping(target = "accountNumberDestin", source = "destinationAccount.accountNumber")
    StatementResponse toResponse(Transaction transaction);

    List<StatementResponse> toResponseList(List<Transaction> transactions);
}
