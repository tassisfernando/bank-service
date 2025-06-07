package br.com.tassisf.bank.repository;

import br.com.tassisf.bank.domain.entity.Account;
import br.com.tassisf.bank.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomer(Customer customer);

    Optional<Account> findByAccountNumberAndCustomer_Id(String accountNumber, UUID customerId);

    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.customer.id = :customerId")
    BigDecimal getTotalBalanceByCustomerId(UUID customerId);
}
