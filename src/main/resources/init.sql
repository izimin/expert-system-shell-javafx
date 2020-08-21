create database es;

create type kind_variable as enum ('output', 'requested', 'output_requested');

alter type kind_variable owner to postgres;

create table es
(
    id   bigserial not null
        constraint es_pk
            primary key,
    name varchar   not null,
    goal bigint
);

comment on table es is 'Экспертная система';

comment on column es.id is 'Идентификатор ЭС';

comment on column es.name is 'Наименование ЭС';

alter table es
    owner to postgres;

create table domain
(
    id      bigserial not null
        constraint domain_pk
            primary key,
    name    varchar   not null,
    es_id   bigint
        constraint domain_es_id_fk
            references es,
    "order" integer   not null
);

comment on table domain is 'Домен';

comment on column domain.id is 'Идентификатор домена';

comment on column domain.name is 'Наименование домена';

comment on column domain."order" is 'Порядковый номер домена';

alter table domain
    owner to postgres;

create unique index domain_name_uindex
    on domain (name);

create table variable
(
    id        bigserial                                     not null
        constraint variable_pk
            primary key,
    name      varchar                                       not null,
    domain_id bigint                                        not null
        constraint variable_domain_id_fk
            references domain,
    es_id     bigint
        constraint variable_es_id_fk
            references es,
    "order"   integer                                       not null,
    kind      kind_variable default 'output'::kind_variable not null,
    question  varchar
);

comment on table variable is 'Переменная';

comment on column variable.id is 'Идентификатор переменной';

comment on column variable.name is 'Наименование переменной';

comment on column variable.domain_id is 'Идентификатор домена переменной';

comment on column variable."order" is 'Порядковый номер переменной';

comment on column variable.question is 'Текст вопроса';

alter table variable
    owner to postgres;

create unique index variable_name_uindex
    on variable (name);

create table rule
(
    id      bigserial not null
        constraint rule_pk
            primary key,
    name    varchar   not null,
    "order" integer   not null,
    es_id   bigint    not null
        constraint rule_es_id_fk
            references es
);

comment on table rule is 'Правило';

comment on column rule.id is 'Идентификатор павила';

comment on column rule.name is 'Наименование правила';

comment on column rule."order" is 'Порядковый номер правила';

alter table rule
    owner to postgres;

create unique index rule_name_uindex
    on rule (name);

create table domain_value
(
    id        bigserial not null
        constraint domain_values_pk
            primary key,
    value     varchar   not null,
    domain_id bigint
        constraint domain_value_domain_id_fk
            references domain,
    "order"   integer
);

comment on table domain_value is 'Все значения доменов';

comment on column domain_value.id is 'Идентификатор значения';

comment on column domain_value.value is 'Значение';

comment on column domain_value.domain_id is 'Идентификатор домена';

comment on column domain_value."order" is 'Порядковый номер';

alter table domain_value
    owner to postgres;

create unique index es_name_uindex
    on es (name);

create table fact
(
    id              bigserial not null
        constraint fact_pk
            primary key,
    variable_id     bigint    not null
        constraint fact_variable_id_fk
            references variable,
    domain_value_id bigint    not null
        constraint fact_domain_value_id_fk
            references domain_value
);

comment on table fact is 'Факты';

comment on column fact.id is 'Идентификатор факта';

alter table fact
    owner to postgres;

create table condition
(
    rule_id bigint  not null
        constraint condition_rule_id_fk
            references rule,
    fact_id bigint  not null
        constraint condition_fact_id_fk
            references fact,
    "order" integer not null,
    constraint condition_pk
        unique (rule_id, fact_id)
);

comment on table condition is 'Условия';

alter table condition
    owner to postgres;

create table conclusion
(
    rule_id bigint  not null
        constraint conclusion_rule_id_fk
            references rule,
    fact_id bigint  not null
        constraint conclusion_fact_id_fk
            references fact,
    "order" integer not null,
    constraint conclusion_pk
        unique (rule_id, fact_id)
);

comment on table conclusion is 'Заключения';

alter table conclusion
    owner to postgres;

create table reason_tree
(
    rule_id        bigint not null,
    parent_rule_id bigint not null
);

alter table reason_tree
    owner to postgres;

create table working_memory
(
    rule_id bigint not null,
    fact_id bigint not null
);

alter table working_memory
    owner to postgres;

