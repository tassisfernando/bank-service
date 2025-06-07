drop table if exists bank.tb_conta;

create table if not exists bank.tb_conta
(
    id         uuid           not null,
    numero     varchar(100)   not null,
    saldo      numeric(15, 2) not null,
    id_cliente uuid           not null,
    primary key (id)
);

drop table if exists bank.tb_lancamento;

create table if not exists bank.tb_lancamento
(
    id       uuid           not null,
    valor    numeric(15, 2) not null,
    tipo     varchar(255)   not null check (tipo in ('CREDIT', 'DEBIT')),
    id_conta uuid           not null,
    primary key (id)
);

alter table if exists bank.tb_conta drop constraint if exists UK2mesboccuugartw582j829omh;

alter table if exists bank.tb_conta add constraint UK2mesboccuugartw582j829omh unique (numero);

alter table if exists bank.tb_conta add constraint FKem9f9hwi50h4c7v1qtb615dv5 foreign key (id_cliente) references bank.tb_cliente;

alter table if exists bank.tb_lancamento add constraint FKlcuxv4r71bxrhsomjxddowoyo foreign key (id_conta) references bank.tb_conta;

alter table if exists bank.tb_lancamento add column data_hora TIMESTAMP not null;