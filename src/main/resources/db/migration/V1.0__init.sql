-- Table Definition ----------------------------------------------

CREATE TABLE counterparty (
                              id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                              phone character varying(12) NOT NULL
);

-- Table Definition ----------------------------------------------

CREATE TABLE "user" (
                        id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        session_end timestamp without time zone NOT NULL DEFAULT now(),
                        roles text NOT NULL DEFAULT 'USER'::text,
                        counterparty_id integer REFERENCES counterparty(id),
                        password text NOT NULL DEFAULT '--'::text
);

-- Table Definition ----------------------------------------------

CREATE TABLE contact_person (
                                id integer GENERATED BY DEFAULT AS IDENTITY,
                                first_name text NOT NULL,
                                last_name text,
                                middle_name text,
                                phone character varying(12),
                                counterparty_id integer,
                                issuer_user_id integer
);

