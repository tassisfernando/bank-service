package br.com.tassisf.bank.infra.auth;

import br.com.tassisf.bank.domain.entity.Customer;

public interface JwtService {

    String generateToken(Customer customer);

    String extractSubject(String token);

    boolean isTokenValid(String token, Customer user);
}
