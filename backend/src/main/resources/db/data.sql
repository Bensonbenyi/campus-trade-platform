-- Default top-level categories. Safe to run repeatedly.
INSERT INTO category (name, parent_id, sort_order, status)
SELECT seed.name, NULL, seed.sort_order, 1
FROM (VALUES
    ('教材', 10),
    ('数码', 20),
    ('宿舍用品', 30),
    ('运动器材', 40),
    ('其他', 50)
) AS seed(name, sort_order)
WHERE NOT EXISTS (
    SELECT 1 FROM category current_category
    WHERE current_category.parent_id IS NULL
      AND current_category.name = seed.name
      AND current_category.deleted = 0
);
