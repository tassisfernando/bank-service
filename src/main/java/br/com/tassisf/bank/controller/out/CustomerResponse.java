package br.com.tassisf.bank.controller.out;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private String id;
    private String name;
    private String cpf;
    private String telephone;
    private BigDecimal totalBalance;
}
