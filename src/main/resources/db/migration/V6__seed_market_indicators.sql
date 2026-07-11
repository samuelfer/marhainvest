-- V6__seed_market_indicators.sql
-- Snapshot consultado em 2026-07-11. DY e DPA: últimos 12 meses.
-- Estratégia temporária: DY exigido de 10%.
-- VIUR11: DPA não usado para preço-teto devido a eventos extraordinários/amortizações.

INSERT INTO asset_market_data
(asset_id, current_price, dividend_yield, pvp, roe, payout, dpa, reference_date)
SELECT a.id, v.current_price, v.dividend_yield, v.pvp, NULL, NULL, v.dpa,
       TIMESTAMP '2026-07-11 00:00:00'
FROM (VALUES
('BBAS3',20.58::numeric,2.65::numeric,0.62::numeric,1.18::numeric),
('BBSE3',40.35::numeric,11.13::numeric,6.20::numeric,4.26::numeric),
('TAEE3',13.66::numeric,7.99::numeric,NULL::numeric,0.99::numeric),
('RAIZ4',0.35::numeric,0.00::numeric,NULL::numeric,0.00::numeric),
('FATN11',79.90::numeric,12.02::numeric,0.82::numeric,9.60::numeric),
('GARE11',8.15::numeric,12.22::numeric,0.88::numeric,1.00::numeric),
('XPLG11',91.55::numeric,10.71::numeric,0.87::numeric,9.84::numeric),
('VISC11',105.64::numeric,9.37::numeric,0.91::numeric,9.87::numeric),
('KNHF11',94.98::numeric,12.63::numeric,0.94::numeric,12.00::numeric),
('XPML11',106.13::numeric,10.38::numeric,0.97::numeric,11.04::numeric),
('GAME11',8.70::numeric,13.45::numeric,0.91::numeric,1.17::numeric),
('VGIA11',9.81::numeric,16.77::numeric,1.01::numeric,1.65::numeric),
('TRXF11',91.10::numeric,12.93::numeric,0.93::numeric,11.81::numeric),
('BIME11',4.93::numeric,17.04::numeric,0.60::numeric,0.87::numeric),
('VGIR11',9.83::numeric,16.67::numeric,1.00::numeric,1.53::numeric),
('VIUR11',2.25::numeric,28.03::numeric,0.64::numeric,NULL::numeric)
) AS v(ticker,current_price,dividend_yield,pvp,dpa)
JOIN asset a ON a.ticker = v.ticker;

UPDATE portfolio_asset pa
SET target_price = latest.dpa / 0.10
FROM (
    SELECT DISTINCT ON (amd.asset_id) amd.asset_id, amd.dpa
    FROM asset_market_data amd
    WHERE amd.reference_date <= TIMESTAMP '2026-07-11 23:59:59'
    ORDER BY amd.asset_id, amd.reference_date DESC, amd.id DESC
) latest
WHERE pa.asset_id = latest.asset_id
  AND latest.dpa IS NOT NULL
  AND latest.dpa > 0;
