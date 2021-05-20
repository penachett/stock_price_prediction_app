create database stonks;
create user penachett with encrypted password 'stonksapp908365';
grant all privileges on all tables in schema public to penachett;
grant all privileges on all sequences in schema public to penachett;
grant all privileges on all functions in schema public to penachett;
create table users (
    id serial primary key,
    login text,
    password text,
    token text default '',
    token_expire date);

create table predictions (
    id serial primary key,
    ticker text,
    create_time bigint,
    predict_time bigint,
    predicted_price double precision,
    user_id integer references users (id));
