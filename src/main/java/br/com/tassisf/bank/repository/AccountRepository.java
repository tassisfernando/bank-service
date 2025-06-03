package br.com.tassisf.bank.repository;

import br.com.tassisf.bank.domain.entity.Account;
import br.com.tassisf.bank.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomer(Customer customer);

    Optional<Account> findByAccountNumberAndCustomer(String accountNumber, Customer customer);

    Optional<Account> findByAccountNumberAndCustomer_Id(String accountNumber, UUID customerId);
}
