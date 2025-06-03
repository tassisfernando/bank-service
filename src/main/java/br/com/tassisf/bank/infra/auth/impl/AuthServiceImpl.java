package br.com.tassisf.bank.infra.auth.impl;

import br.com.tassisf.bank.controller.in.AuthRequest;
import br.com.tassisf.bank.controller.out.AuthResponse;
import br.com.tassisf.bank.exception.InvalidCredentialsException;
import br.com.tassisf.bank.exception.ResourceNotFoundException;
import br.com.tassisf.bank.infra.auth.AuthService;
import br.com.tassisf.bank.infra.auth.JwtService;
import br.com.tassisf.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        var user = repository.findByCpf(request.cpf())
                .orElseThrow(() -> new ResourceNotFoundException("CPF inválido"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.error("Senha inválida para o usuário: {}", request.cpf());
            throw new InvalidCredentialsException("Senha inválida");
        }

        log.info("Usuário autenticado com sucesso: {}", user.getCpf());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getName(), user.getId());
    }
}
