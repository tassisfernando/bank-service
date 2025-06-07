package br.com.tassisf.bank.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbConta", schema = "bank")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "numero", nullable = false, length = 100, unique = true)
    private String accountNumber;

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "limiteCredito", nullable = false, precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCliente", referencedColumnName = "id", nullable = false)
    private Customer customer;
}
