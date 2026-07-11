CREATE TABLE asset_market_data
(
    id             BIGSERIAL PRIMARY KEY,
    asset_id       BIGINT         NOT NULL,
    current_price  NUMERIC(19, 4) NOT NULL,
    dividend_yield NUMERIC(10, 4),
    pvp            NUMERIC(10, 4),
    roe            NUMERIC(10, 4),
    payout         NUMERIC(10, 4),
    reference_date TIMESTAMP      NOT NULL,

    CONSTRAINT fk_asset_market_data_asset
        FOREIGN KEY (asset_id)
            REFERENCES asset (id)
);

CREATE INDEX idx_asset_market_data_asset
    ON asset_market_data (asset_id);

CREATE INDEX idx_asset_market_data_reference
    ON asset_market_data (reference_date);