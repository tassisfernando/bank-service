package br.com.tassisf.bank.repository;

import br.com.tassisf.bank.domain.entity.Account;
import br.com.tassisf.bank.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
