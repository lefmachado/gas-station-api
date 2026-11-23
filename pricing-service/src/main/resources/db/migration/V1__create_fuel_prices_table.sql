CREATE TABLE fuel_prices (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    liter_price NUMERIC(10, 3) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_fuel_prices_name UNIQUE (name)
);
