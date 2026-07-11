INSERT INTO asset_market_data (
    asset_id,
    current_price,
    dividend_yield,
    pvp,
    roe,
    payout,
    dpa,
    reference_date
)
SELECT
    amd.asset_id,
    amd.current_price,
    amd.dividend_yield,
    amd.pvp,
    amd.roe,
    amd.payout,
    amd.dpa,
    CURRENT_TIMESTAMP
FROM asset_market_data amd
WHERE amd.id IN (
    SELECT DISTINCT ON (asset_id)
        id
    FROM asset_market_data
    WHERE dividend_yield IS NOT NULL
       OR pvp IS NOT NULL
       OR dpa IS NOT NULL
    ORDER BY
        asset_id,
        reference_date DESC,
        id DESC
);

UPDATE portfolio_asset pa
SET target_price = latest.dpa / 0.10
FROM (
    SELECT DISTINCT ON (asset_id)
        asset_id,
        dpa
    FROM asset_market_data
    WHERE dpa IS NOT NULL
      AND dpa > 0
    ORDER BY
        asset_id,
        reference_date DESC,
        id DESC
) latest
WHERE pa.asset_id = latest.asset_id;