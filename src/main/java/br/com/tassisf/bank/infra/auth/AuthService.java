package br.com.tassisf.bank.infra.auth;

import br.com.tassisf.bank.controller.in.AuthRequest;
import br.com.tassisf.bank.controller.out.AuthResponse;

public interface AuthService {

    AuthResponse authenticate(AuthRequest request);
}
