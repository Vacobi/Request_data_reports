CREATE OR REPLACE VIEW group_request_stats AS
WITH filter AS (
    SELECT * FROM request_filters
    WHERE id = (SELECT MAX(id) FROM request_filters)
),
filtered_requests AS (
    SELECT r.*
    FROM requests r
    WHERE (f.from_date IS NULL OR r.created_at >= f.from_date)
    AND (f.to_date IS NULL OR r.created_at >= f.to_date)
    AND (f.host IS NULL OR r.host = f.host)
    AND (f.path IS NULL OR r.path = f.path)
),
request_stats AS (
    SELECT
        r.id,
        r.url,
        r.host,
        r.path,
        (SELECT COUNT(*) FROM headers h
         WHERE h.request_id = r.id) AS header_count,
        (SELECT COUNT(*) FROM query_params qp
         WHERE qp.request_id = r.id) AS param_count
    FROM filtered_requests r
)
SELECT
    rs.host,
    rs.path,
    AVG(rs.header_count) AS avg_headers,
    AVG(rs.param_count) AS avg_params
FROM request_stats rs
CROSS JOIN filter f
GROUP BY rs.host, rs.path
HAVING
    (f.avg_headers IS NULL OR AVG(rs.header_count) = f.avg_headers) AND
    (f.avg_query_params IS NULL OR AVG(rs.param_count) = f.avg_query_params);