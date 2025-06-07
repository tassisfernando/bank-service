package br.com.tassisf.bank.domain.entity;

import br.com.tassisf.bank.domain.enums.Operation;
import br.com.tassisf.bank.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbLancamento", schema = "bank")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "dataHora", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "operacao", nullable = false)
    private Operation operation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta_origem", referencedColumnName = "id", nullable = false)
    private Account originAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta_destino", referencedColumnName = "id")
    private Account destinationAccount;
}
