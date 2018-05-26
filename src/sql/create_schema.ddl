-- Generated by Oracle SQL Developer Data Modeler 17.4.0.355.2121
--   at:        2018-05-26 16:14:37 CEST
--   site:      Oracle Database 11g
--   type:      Oracle Database 11g



CREATE TABLE assortment (
    warehouse_id       NUMBER NOT NULL,
    document_item_id   NUMBER NOT NULL,
    batch_number       VARCHAR2(50)
);

ALTER TABLE assortment ADD CONSTRAINT assortment_pk PRIMARY KEY ( warehouse_id,
document_item_id );

CREATE TABLE contractor_types (
    type_id   NUMBER NOT NULL,
    name      VARCHAR2(30)
);

ALTER TABLE contractor_types ADD CONSTRAINT contractor_types_pk PRIMARY KEY ( type_id );

CREATE TABLE contractors (
    c_name               VARCHAR2(100),
    first_name           VARCHAR2(32),
    last_name            VARCHAR2(32),
    city                 VARCHAR2(32),
    street               VARCHAR2(32),
    email                VARCHAR2(32),
    phone                VARCHAR2(32),
    buyer_or_seller      VARCHAR2(2),
    contractor_type_id   NUMBER NOT NULL,
    nip                  VARCHAR2(10),
    pesel                VARCHAR2(11),
    contractor_id        NUMBER NOT NULL
);

COMMENT ON COLUMN contractors.buyer_or_seller IS
    'B/S/BS';

ALTER TABLE contractors ADD CONSTRAINT contractors_pk PRIMARY KEY ( contractor_id );

CREATE TABLE document_types (
    type_id   NUMBER NOT NULL,
    name      VARCHAR2(30),
    tag       NUMBER
);

ALTER TABLE document_types ADD CONSTRAINT document_types_pk PRIMARY KEY ( type_id );

CREATE TABLE documents (
    issue_date         DATE,
    d_name             VARCHAR2(32),
    document_id        NUMBER NOT NULL,
    contractor_id      NUMBER NOT NULL,
    worker_id          NUMBER NOT NULL,
    document_type_id   NUMBER NOT NULL
);

ALTER TABLE documents ADD CONSTRAINT documents_pk PRIMARY KEY ( document_id );

CREATE TABLE documents_items (
    transaction_sign   INTEGER,
    quantity           FLOAT,
    price              FLOAT,
    vat_rate_id        NUMBER NOT NULL,
    tax                FLOAT,
    margin             FLOAT,
    batch_number       VARCHAR2(50),
    document_item_id   NUMBER NOT NULL,
    item_id            NUMBER NOT NULL,
    document_id        NUMBER NOT NULL,
    margin_types_id    NUMBER NOT NULL
);

ALTER TABLE documents_items ADD CONSTRAINT documents_items_pk PRIMARY KEY ( document_item_id );

CREATE TABLE items (
    ean                   VARCHAR2(13),
    item_model_number     VARCHAR2(30),
    i_name                VARCHAR2(50),
    current_price         FLOAT,
    desired_quantity      FLOAT,
    description           CLOB,
    item_id               NUMBER NOT NULL,
    vat_rate_id           NUMBER NOT NULL,
    measurement_unit_id   NUMBER NOT NULL
);

ALTER TABLE items ADD CONSTRAINT items_pk PRIMARY KEY ( item_id );

CREATE TABLE margin_types (
    type_id     NUMBER NOT NULL,
    type_name   VARCHAR2(15)
);

ALTER TABLE margin_types ADD CONSTRAINT margin_types_pk PRIMARY KEY ( type_id );

CREATE TABLE measurement_units (
    unit_id     NUMBER NOT NULL,
    unit_name   VARCHAR2(15)
);

ALTER TABLE measurement_units ADD CONSTRAINT measurement_units_pk PRIMARY KEY ( unit_id );

CREATE TABLE messages (
    m_date         DATE,
    message        CLOB,
    sender_id      NUMBER NOT NULL,
    recipient_id   NUMBER NOT NULL,
    msg_id         NUMBER NOT NULL
);

ALTER TABLE messages ADD CONSTRAINT messages_pk PRIMARY KEY ( msg_id );

CREATE TABLE messages_notifications (
    sender_id      NUMBER NOT NULL,
    recipient_id   NUMBER NOT NULL,
    ack            CHAR(1) NOT NULL
);

ALTER TABLE messages_notifications ADD CONSTRAINT messages_notifications_pk PRIMARY KEY ( sender_id,
recipient_id );

CREATE TABLE vat_rates (
    vat_rate_id   NUMBER NOT NULL,
    tax           FLOAT,
    r_name        VARCHAR2(30)
);

ALTER TABLE vat_rates ADD CONSTRAINT vat_rates_pk PRIMARY KEY ( vat_rate_id );

CREATE TABLE warehouses (
    warehouse_id   NUMBER NOT NULL,
    w_name         VARCHAR2(30)
);

ALTER TABLE warehouses ADD CONSTRAINT warehouses_pk PRIMARY KEY ( warehouse_id );

CREATE TABLE workers (
    first_name     VARCHAR2(32),
    last_name      VARCHAR2(32),
    city           VARCHAR2(32),
    street         VARCHAR2(32),
    email          VARCHAR2(32),
    phone          VARCHAR2(32),
    pesel          VARCHAR2(32),
    ip_address     VARCHAR2(32),
    u_password     VARCHAR2(32),
    worker_id      NUMBER NOT NULL,
    warehouse_id   NUMBER
);

ALTER TABLE workers ADD CONSTRAINT workers_pk PRIMARY KEY ( worker_id );

ALTER TABLE assortment
    ADD CONSTRAINT assortment_documents_items_fk FOREIGN KEY ( document_item_id )
        REFERENCES documents_items ( document_item_id );

ALTER TABLE assortment
    ADD CONSTRAINT assortment_warehouses_fk FOREIGN KEY ( warehouse_id )
        REFERENCES warehouses ( warehouse_id );

ALTER TABLE contractors
    ADD CONSTRAINT contractor_types_fk FOREIGN KEY ( contractor_type_id )
        REFERENCES contractor_types ( type_id );

ALTER TABLE documents_items
    ADD CONSTRAINT docmnts_items_margin_types_fk FOREIGN KEY ( margin_types_id )
        REFERENCES margin_types ( type_id );

ALTER TABLE documents
    ADD CONSTRAINT documents_contractors_fk FOREIGN KEY ( contractor_id )
        REFERENCES contractors ( contractor_id );

ALTER TABLE documents
    ADD CONSTRAINT documents_document_types_fk FOREIGN KEY ( document_type_id )
        REFERENCES document_types ( type_id );

ALTER TABLE documents_items
    ADD CONSTRAINT documents_items_documents_fk FOREIGN KEY ( document_id )
        REFERENCES documents ( document_id );

ALTER TABLE documents_items
    ADD CONSTRAINT documents_items_items_fk FOREIGN KEY ( item_id )
        REFERENCES items ( item_id );

ALTER TABLE documents_items
    ADD CONSTRAINT documents_items_vat_rates_fk FOREIGN KEY ( vat_rate_id )
        REFERENCES vat_rates ( vat_rate_id );

ALTER TABLE documents
    ADD CONSTRAINT documents_workers_fk FOREIGN KEY ( worker_id )
        REFERENCES workers ( worker_id );

ALTER TABLE items
    ADD CONSTRAINT items_measurmnt_units_fk FOREIGN KEY ( measurement_unit_id )
        REFERENCES measurement_units ( unit_id );

ALTER TABLE items
    ADD CONSTRAINT items_vat_rates_fk FOREIGN KEY ( vat_rate_id )
        REFERENCES vat_rates ( vat_rate_id );

ALTER TABLE messages_notifications
    ADD CONSTRAINT messages_notif_recipient_fk FOREIGN KEY ( recipient_id )
        REFERENCES workers ( worker_id );

ALTER TABLE messages_notifications
    ADD CONSTRAINT messages_notif_sender_fk FOREIGN KEY ( sender_id )
        REFERENCES workers ( worker_id );

ALTER TABLE messages
    ADD CONSTRAINT messages_workers_fk FOREIGN KEY ( sender_id )
        REFERENCES workers ( worker_id );

ALTER TABLE messages
    ADD CONSTRAINT messages_workers_fkv2 FOREIGN KEY ( recipient_id )
        REFERENCES workers ( worker_id );

ALTER TABLE workers
    ADD CONSTRAINT workers_warehouses_fk FOREIGN KEY ( warehouse_id )
        REFERENCES warehouses ( warehouse_id );

CREATE SEQUENCE contractor_types_type_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER contractor_types_type_id_trg BEFORE
    INSERT ON contractor_types
    FOR EACH ROW
    WHEN ( new.type_id IS NULL )
BEGIN
    :new.type_id := contractor_types_type_id_seq.nextval;
END;
/

CREATE SEQUENCE contractors_contractor_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER contractors_contractor_id_trg BEFORE
    INSERT ON contractors
    FOR EACH ROW
    WHEN ( new.contractor_id IS NULL )
BEGIN
    :new.contractor_id := contractors_contractor_id_seq.nextval;
END;
/

CREATE SEQUENCE document_types_type_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER document_types_type_id_trg BEFORE
    INSERT ON document_types
    FOR EACH ROW
    WHEN ( new.type_id IS NULL )
BEGIN
    :new.type_id := document_types_type_id_seq.nextval;
END;
/

CREATE SEQUENCE documents_document_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER documents_document_id_trg BEFORE
    INSERT ON documents
    FOR EACH ROW
    WHEN ( new.document_id IS NULL )
BEGIN
    :new.document_id := documents_document_id_seq.nextval;
END;
/

CREATE SEQUENCE documents_items_document_item_ START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER documents_items_document_item_ BEFORE
    INSERT ON documents_items
    FOR EACH ROW
    WHEN ( new.document_item_id IS NULL )
BEGIN
    :new.document_item_id := documents_items_document_item_.nextval;
END;
/

CREATE SEQUENCE items_item_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER items_item_id_trg BEFORE
    INSERT ON items
    FOR EACH ROW
    WHEN ( new.item_id IS NULL )
BEGIN
    :new.item_id := items_item_id_seq.nextval;
END;
/

CREATE SEQUENCE margin_types_type_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER margin_types_type_id_trg BEFORE
    INSERT ON margin_types
    FOR EACH ROW
    WHEN ( new.type_id IS NULL )
BEGIN
    :new.type_id := margin_types_type_id_seq.nextval;
END;
/

CREATE SEQUENCE measurement_units_unit_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER measurement_units_unit_id_trg BEFORE
    INSERT ON measurement_units
    FOR EACH ROW
    WHEN ( new.unit_id IS NULL )
BEGIN
    :new.unit_id := measurement_units_unit_id_seq.nextval;
END;
/

CREATE SEQUENCE messages_msg_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER messages_msg_id_trg BEFORE
    INSERT ON messages
    FOR EACH ROW
    WHEN ( new.msg_id IS NULL )
BEGIN
    :new.msg_id := messages_msg_id_seq.nextval;
END;
/

CREATE SEQUENCE vat_rates_vat_rate_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER vat_rates_vat_rate_id_trg BEFORE
    INSERT ON vat_rates
    FOR EACH ROW
    WHEN ( new.vat_rate_id IS NULL )
BEGIN
    :new.vat_rate_id := vat_rates_vat_rate_id_seq.nextval;
END;
/

CREATE SEQUENCE warehouses_warehouse_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER warehouses_warehouse_id_trg BEFORE
    INSERT ON warehouses
    FOR EACH ROW
    WHEN ( new.warehouse_id IS NULL )
BEGIN
    :new.warehouse_id := warehouses_warehouse_id_seq.nextval;
END;
/

CREATE SEQUENCE workers_worker_id_seq START WITH 1 NOCACHE ORDER;

CREATE OR REPLACE TRIGGER workers_worker_id_trg BEFORE
    INSERT ON workers
    FOR EACH ROW
    WHEN ( new.worker_id IS NULL )
BEGIN
    :new.worker_id := workers_worker_id_seq.nextval;
END;
/



-- Oracle SQL Developer Data Modeler Summary Report: 
-- 
-- CREATE TABLE                            14
-- CREATE INDEX                             0
-- ALTER TABLE                             31
-- CREATE VIEW                              0
-- ALTER VIEW                               0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                          12
-- ALTER TRIGGER                            0
-- CREATE COLLECTION TYPE                   0
-- CREATE STRUCTURED TYPE                   0
-- CREATE STRUCTURED TYPE BODY              0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                         12
-- CREATE MATERIALIZED VIEW                 0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
-- 
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
-- 
-- REDACTION POLICY                         0
-- 
-- ORDS DROP SCHEMA                         0
-- ORDS ENABLE SCHEMA                       0
-- ORDS ENABLE OBJECT                       0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0
