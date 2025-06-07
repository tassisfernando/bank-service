package br.com.tassisf.bank.controller.in;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private String accountNumberOrigin;
    private String accountNumberDestin;
    private BigDecimal amount;
}
