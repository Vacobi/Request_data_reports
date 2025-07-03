alter table reports
drop column if exists report_file_id;

alter table report_files
drop constraint if exists report_files_report_id_key;

alter table report_files
add constraint unique_report_mime_type unique (report_id, mime_type);