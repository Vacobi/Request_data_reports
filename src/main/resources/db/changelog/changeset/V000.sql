create table if not exists requests (
    id bigint primary key generated always as identity unique,
    url text not null,
    path text,
    body text,
    created_at timestamptz default current_timestamp
);

create table if not exists headers (
    id bigint primary key generated always as identity unique,
    name text not null,
    value text not null,
    request_id bigint not null,
    constraint fk_request_headers foreign key (request_id) references requests(id) on delete cascade
);

create table if not exists query_params (
    id bigint primary key generated always as identity unique,
    name text not null,
    value text not null,
    request_id bigint not null,
    constraint fk_request_query_params foreign key (request_id) references requests(id) on delete cascade
);
