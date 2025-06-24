create index if not exists idx_headers_request_id on headers(request_id);
create index if not exists idx_query_params_request_id on query_params(request_id);
create index if not exists idx_reports_filter_id on reports(filter_id);
create index if not exists idx_report_rows_report_id on report_rows(report_id);
create index if not exists idx_reports_report_file_id on reports(report_file_id);
create index if not exists idx_report_files_report_id on report_files(report_id);

create index if not exists idx_requests_url on requests(url);
create index if not exists idx_requests_url_path on requests(url, path);