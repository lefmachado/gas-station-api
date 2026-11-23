CREATE TABLE fuel_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    liter_price NUMERIC(10, 3) NOT NULL,
    CONSTRAINT uk_fuel_types_name UNIQUE (name)
);

CREATE TABLE fuel_pumps (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    fuel_type_id BIGINT NOT NULL,
    CONSTRAINT fk_fuel_pumps_fuel_type
        FOREIGN KEY (fuel_type_id) REFERENCES fuel_types (id)
);

CREATE TABLE refuelings (
    id BIGSERIAL PRIMARY KEY,
    fuel_pump_id BIGINT NOT NULL,
    refueled_at TIMESTAMP NOT NULL,
    liters NUMERIC(10, 3) NOT NULL,
    applied_liter_price NUMERIC(10, 3) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_refuelings_fuel_pump
        FOREIGN KEY (fuel_pump_id) REFERENCES fuel_pumps (id)
);

CREATE INDEX idx_fuel_pumps_fuel_type_id ON fuel_pumps (fuel_type_id);
CREATE INDEX idx_refuelings_fuel_pump_id ON refuelings (fuel_pump_id);
CREATE INDEX idx_refuelings_refueled_at ON refuelings (refueled_at);
