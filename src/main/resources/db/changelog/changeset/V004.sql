alter table reports
add column if not exists report_file_id bigint;

create table if not exists report_files (
    id bigint primary key generated always as identity unique,
    report_id bigint not null unique references reports(id) on delete cascade,
    file_name text not null,
    file_path text,
    storage_type varchar(10) not null,
    mime_type text not null,
    file_data BYTEA,
    created_at timestamptz default current_timestamp

    constraint chk_storage_type check (
        (storage_type = 'DATABASE' or storage_type = 'DISK')
        ),
    constraint chk_storage_type_consistency check (
        ((storage_type = 'DATABASE' and file_path is null) and
         (storage_type = 'DATABASE' and file_data is not null)) or
        ((storage_type = 'DISK' and file_path is not null) and
         (storage_type = 'DISK' and file_data is null))
        )
);