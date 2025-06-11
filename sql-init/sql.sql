CREATE DATABASE springai;



create table base_conversation
(
    id                bigserial,
    conversation_id   varchar(32) not null,
    content           text,
    created_time      timestamp(6) not null,
    updated_time      timestamp(6) not null,
    title             varchar(512)
);

create table base_doc
(
    id              bigserial,
    doc_id          varchar(40) not null,
    modified_time   varchar(20),
    created_time    timestamp(6) not null,
    updated_time    timestamp(6) not null,
    doc_name        varchar(256),
    url             varchar(512),
    vector_doc_id   varchar(40)
);

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS vector_store (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    content text,
    metadata json,
    embedding vector(384)
);

CREATE INDEX ON vector_store USING HNSW (embedding vector_cosine_ops);
