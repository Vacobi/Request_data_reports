CREATE OR REPLACE VIEW group_request_stats AS
WITH filter AS (
    SELECT * FROM filters_request
    WHERE id = (SELECT MAX(id) FROM filters_request)
),
filtered_requests AS (
    SELECT r.*
    FROM requests r
    JOIN filter f ON TRUE
    WHERE (f.from_date IS NULL OR r.created_at >= f.from_date)
    AND (f.to_date IS NULL OR r.created_at <= f.to_date)
    AND (f.host IS NULL OR r.url = f.host)
    AND (f.path IS NULL OR r.path = f.path)
),
request_stats AS (
    SELECT
    r.id,
    r.url AS host,
    r.path,
    (SELECT COUNT(*) FROM headers h
    WHERE h.request_id = r.id) AS header_count,
    (SELECT COUNT(*) FROM query_params qp
    WHERE qp.request_id = r.id) AS param_count
    FROM filtered_requests r
)
SELECT
    gen_random_uuid() as id,
    rs.host,
    rs.path,
    AVG(rs.header_count) AS avg_headers,
    AVG(rs.param_count) AS avg_params
FROM request_stats rs
JOIN filter f ON TRUE
GROUP BY rs.host, rs.path
HAVING
    (MAX(f.avg_headers) IS NULL OR ROUND(AVG(rs.header_count), 2) = ROUND(MAX(f.avg_headers), 2)) AND
    (MAX(f.avg_query_params) IS NULL OR ROUND(AVG(rs.param_count), 2) = ROUND(MAX(f.avg_query_params), 2));