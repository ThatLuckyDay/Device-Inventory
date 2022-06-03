-- This script was generated by a beta version of the ERD tool in pgAdmin 4.
-- Please log an issue at https://redmine.postgresql.org/projects/pgadmin4/issues/new if you find any bugs, including reproduction steps.
BEGIN;


CREATE TABLE IF NOT EXISTS "app-db"."app-db_schema_history"
(
    installed_rank integer NOT NULL,
    version character varying(50) COLLATE pg_catalog."default",
    description character varying(200) COLLATE pg_catalog."default" NOT NULL,
    type character varying(20) COLLATE pg_catalog."default" NOT NULL,
    script character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    checksum integer,
    installed_by character varying(100) COLLATE pg_catalog."default" NOT NULL,
    installed_on timestamp without time zone NOT NULL DEFAULT now(),
    execution_time integer NOT NULL,
    success boolean NOT NULL,
    CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank)
);

CREATE TABLE IF NOT EXISTS "app-db"."user"
(
    id bigserial NOT NULL,
    "firstName" character varying(40)[] NOT NULL,
    "lastName" character varying(40)[] NOT NULL,
    patronymic character varying(40)[],
    login character varying(40)[] NOT NULL,
    password character varying(40)[] NOT NULL,
    "roleId" integer NOT NULL,
    active boolean NOT NULL DEFAULT true,
    email character varying(255)[] NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS "app-db".device
(
    id bigserial NOT NULL,
    "deviceName" character varying(40)[] NOT NULL,
    "userId" bigint,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS "app-db".session
(
    session uuid NOT NULL,
    "userId" bigint NOT NULL
);

CREATE TABLE IF NOT EXISTS "app-db".role
(
    id serial NOT NULL,
    authority character varying(40) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_id UNIQUE (id)
);

ALTER TABLE IF EXISTS "app-db"."user"
    ADD CONSTRAINT "user_roleId" FOREIGN KEY ("roleId")
    REFERENCES "app-db".role (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS "app-db".device
    ADD CONSTRAINT "device_userId" FOREIGN KEY ("userId")
    REFERENCES "app-db"."user" (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS "app-db".session
    ADD CONSTRAINT "session_userId" FOREIGN KEY ("userId")
    REFERENCES "app-db"."user" (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE
    NOT VALID;

ALTER ROLE admin SET search_path TO "app-db";

END;