package br.com.tassisf.bank.controller;

import br.com.tassisf.bank.controller.in.AuthRequest;
import br.com.tassisf.bank.controller.out.AuthResponse;
import br.com.tassisf.bank.infra.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        log.info("Inicia Login do usuário: {}", request.cpf());
        return ResponseEntity.ok(authService.authenticate(request));
    }
}