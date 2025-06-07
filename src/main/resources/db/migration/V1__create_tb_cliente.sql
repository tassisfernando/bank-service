drop table if exists bank.tb_cliente;

create table if not exists bank.tb_cliente
(
    id       uuid         not null,
    cpf      varchar(20)  not null,
    nome     varchar(200) not null,
    telefone varchar(20)  not null,
    primary key (id)
);

alter table if exists bank.tb_cliente drop constraint if exists UKjgra977gi05fur83l225x4qkr;

alter table if exists bank.tb_cliente add constraint UKjgra977gi05fur83l225x4qkr unique (cpf);

alter table if exists bank.tb_cliente alter column cpf set data type varchar (11);

alter table if exists bank.tb_cliente add column password_hash varchar (60) not null;

alter table if exists bank.tb_cliente alter column telefone set data type varchar (15);
