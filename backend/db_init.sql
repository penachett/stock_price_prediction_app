create database stonks;
create user penachett with encrypted password 'stonksapp908365';
grant all privileges on all tables in schema public to penachett;
grant all privileges on all sequences in schema public to penachett;
grant all privileges on all functions in schema public to penachett;
create table users (
    id serial primary key,
    login text not null,
    password text not null,
    token text default '',
    token_expire date not null);

create table predictions (
    id serial primary key,
    ticker text not null,
    create_time bigint not null,
    predict_time bigint not null,
    predicted_price double precision not null,
    start_price double precision not null,
    user_id bigint references users (id));
