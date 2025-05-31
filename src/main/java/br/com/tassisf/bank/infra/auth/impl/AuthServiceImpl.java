package br.com.tassisf.bank.infra.auth.impl;

import br.com.tassisf.bank.controller.in.AuthRequest;
import br.com.tassisf.bank.controller.out.AuthResponse;
import br.com.tassisf.bank.infra.auth.AuthService;
import br.com.tassisf.bank.infra.auth.JwtService;
import br.com.tassisf.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        var user = repository.findByCpf(request.cpf())
                .orElseThrow(() -> new RuntimeException("CPF inválido"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
