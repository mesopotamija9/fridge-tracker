CREATE TABLE IF NOT EXISTS public.fridge_item (
    id VARCHAR(36) PRIMARY KEY,
    fridge_id VARCHAR(36) REFERENCES fridge(id),
    item_id VARCHAR(36) REFERENCES item(id),
    stored_at TIMESTAMP NOT NULL,
    best_before_date TIMESTAMP NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_fridge_item_best_before_date
ON public.fridge_item USING BTREE (best_before_date);