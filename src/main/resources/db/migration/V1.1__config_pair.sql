CREATE TABLE contact_warehouse_pair
(
    id                integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    contact_person_id integer NOT NULL REFERENCES contact_person (id),
    warehouse_ref     text    NOT NULL
);
