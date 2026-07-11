-- V3__seed_development_portfolio.sql
-- Dados transcritos dos prints enviados.
-- GARE11 considerado com 910 cotas, conforme o print atual.

INSERT INTO portfolio (name)
SELECT 'Carteira Principal'
WHERE NOT EXISTS (SELECT 1 FROM portfolio WHERE name = 'Carteira Principal');

INSERT INTO asset (ticker, description, asset_type, segment)
VALUES
('BBAS3','Banco do Brasil','STOCK',NULL), ('BBSE3','BB Seguridade','STOCK',NULL),
('TAEE3','Taesa','STOCK',NULL), ('RAIZ4','Raizen','STOCK',NULL),
('FATN11',NULL,'FII','Lajes Corporativas'), ('GARE11',NULL,'FII','Híbrido'),
('XPLG11',NULL,'FII','Logístico'), ('VISC11',NULL,'FII','Shoppings'),
('KNHF11',NULL,'FII','Híbrido'), ('XPML11',NULL,'FII','Shoppings'),
('GAME11',NULL,'FII','Outros'), ('VGIA11',NULL,'FII','Fiagros'),
('TRXF11',NULL,'FII','Híbrido'), ('BIME11',NULL,'FII','Híbrido'),
('VGIR11',NULL,'FII','Títulos e Valores Mobiliários'), ('VIUR11',NULL,'FII','Outros')
ON CONFLICT (ticker) DO NOTHING;

INSERT INTO portfolio_asset (portfolio_id, asset_id, quantity, average_price, target_price)
SELECT p.id, a.id, v.quantity, v.average_price, NULL
FROM (VALUES
('BBAS3',505,26.52::numeric),('BBSE3',122,33.93::numeric),('TAEE3',50,11.53::numeric),
('RAIZ4',1,3.58::numeric),('FATN11',100,84.48::numeric),('GARE11',910,8.71::numeric),
('XPLG11',80,99.16::numeric),('VISC11',50,118.03::numeric),('KNHF11',33,97.40::numeric),
('XPML11',26,99.46::numeric),('GAME11',304,8.90::numeric),('VGIA11',206,9.22::numeric),
('TRXF11',22,91.69::numeric),('BIME11',176,7.27::numeric),('VGIR11',50,9.28::numeric),
('VIUR11',121,6.38::numeric)
) AS v(ticker, quantity, average_price)
JOIN asset a ON a.ticker = v.ticker
CROSS JOIN portfolio p
WHERE p.name = 'Carteira Principal'
ON CONFLICT (portfolio_id, asset_id) DO NOTHING;

INSERT INTO goal (portfolio_asset_id, target_quantity)
SELECT pa.id, v.target_quantity
FROM (VALUES ('GARE11',1000),('XPLG11',200),('TRXF11',100)) AS v(ticker,target_quantity)
JOIN asset a ON a.ticker = v.ticker
JOIN portfolio_asset pa ON pa.asset_id = a.id
JOIN portfolio p ON p.id = pa.portfolio_id
WHERE p.name = 'Carteira Principal'
ON CONFLICT (portfolio_asset_id) DO NOTHING;

INSERT INTO asset_market_data (asset_id, current_price, reference_date)
SELECT a.id, v.current_price, CURRENT_TIMESTAMP
FROM (VALUES
('BBAS3',20.64::numeric),('BBSE3',40.40::numeric),('TAEE3',13.69::numeric),
('RAIZ4',0.35::numeric),('FATN11',79.90::numeric),('GARE11',8.15::numeric),
('XPLG11',91.89::numeric),('VISC11',105.64::numeric),('KNHF11',94.98::numeric),
('XPML11',106.34::numeric),('GAME11',8.70::numeric),('VGIA11',9.81::numeric),
('TRXF11',91.24::numeric),('BIME11',4.93::numeric),('VGIR11',9.84::numeric),
('VIUR11',2.25::numeric)
) AS v(ticker,current_price)
JOIN asset a ON a.ticker = v.ticker;
