drop table stamp;

CREATE TABLE stamp (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxstamp on
    stamp
        using gin(data) ;
		
GRANT ALL on all tables in schema public to aquarius with grant option; 

INSERT INTO stamp ("data") VALUES (
'{"id": "default_aceptado", "name": "Default Aceptado", "size": 34267, "mimeType": "image/png", "createdBy": "ecm", "createdOn": {"time": 1526748896988, "timezone": "America/Mexico_City"}, "extension": ""}');
INSERT INTO stamp ("data") VALUES (
'{"id": "default_pagado", "name": "Default Pagado", "size": 27067, "mimeType": "image/png", "createdBy": "ecm", "createdOn": {"time": 1526748928650, "timezone": "America/Mexico_City"}, "extension": ""}');
INSERT INTO stamp ("data") VALUES (
'{"id": "default_rechazado", "name": "Default Rechazado", "size": 35044, "mimeType": "image/png", "createdBy": "ecm", "createdOn": {"time": 1526748946931, "timezone": "America/Mexico_City"}, "extension": ""}');
INSERT INTO stamp ("data") VALUES (
'{"id": "default_recibido", "name": "Default Recibido", "size": 29557, "mimeType": "image/png", "createdBy": "ecm", "createdOn": {"time": 1526748961459, "timezone": "America/Mexico_City"}, "extension": ""}');
