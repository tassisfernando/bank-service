-- Índices para Account
CREATE INDEX idx_account_number ON bank.tb_conta (numero);
CREATE INDEX idx_account_customer ON bank.tb_conta (id_cliente);

-- Índices para Transaction
CREATE INDEX idx_transaction_account ON bank.tb_lancamento (id_conta_origem);
CREATE INDEX idx_transaction_account_date ON bank.tb_lancamento (id_conta_destino);

-- Índices para Customer
CREATE INDEX idx_customer_cpf ON bank.tb_cliente (cpf);