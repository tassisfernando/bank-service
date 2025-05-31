package br.com.tassisf.bank.controller.in;

public record CustomerRequest(String name, String cpf, String telephone, String password) {
}
