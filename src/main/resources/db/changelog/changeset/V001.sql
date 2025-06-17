create table if not exists request_filters (
    id bigint primary key generated always as identity unique,
    from_date timestamptz,
    to_date timestamptz,
    host text,
    path text,
    avg_headers integer,
    avg_query_params integer
);