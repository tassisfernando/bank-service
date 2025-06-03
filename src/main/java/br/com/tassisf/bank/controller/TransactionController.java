package br.com.tassisf.bank.controller;

import br.com.tassisf.bank.business.TransactionService;
import br.com.tassisf.bank.controller.in.TransactionRequest;
import br.com.tassisf.bank.controller.out.StatementResponse;
import br.com.tassisf.bank.controller.out.TransactionResponse;
import br.com.tassisf.bank.domain.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionRequest request,
                                                       @RequestHeader("X-Customer-Id") String customerIdStr) {
        UUID customerId = UUID.fromString(customerIdStr);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.process(request, TransactionType.CREDIT, customerId));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody TransactionRequest request,
                                                        @RequestHeader("X-Customer-Id") String customerIdStr) {
        UUID customerId = UUID.fromString(customerIdStr);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.process(request, TransactionType.DEBIT, customerId));
    }

    @GetMapping("/statement/{accountId}")
    public ResponseEntity<List<StatementResponse>> getAccountStatement(@PathVariable String accountId,
                                                                       @RequestHeader("X-Customer-Id") String customerIdStr) {
        UUID customerId = UUID.fromString(customerIdStr);
        return ResponseEntity.ok(transactionService.getAccountStatement(accountId, customerId));
    }
}
