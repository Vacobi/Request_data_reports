create table if not exists reports (
    id bigint primary key generated always as identity unique,
    filter_id bigint,
    status varchar(20) not null,
    created_at timestamptz default current_timestamp,
    finished_at timestamptz
);

create table if not exists report_rows (
    id bigint primary key generated always as identity unique,
    report_id bigint not null,
    row_uuid UUID not null,
    host text not null,
    path text,
    avg_headers decimal(5, 2) not null,
    avg_query_params decimal(5, 2) not null,
    constraint fk_report foreign key (report_id) references reports(id)
);