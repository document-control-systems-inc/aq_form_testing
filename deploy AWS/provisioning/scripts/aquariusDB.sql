CREATE TABLE configuration (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxconfiguration on
    configuration
        using gin(data) ;
		
CREATE TABLE error (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxerrorid on
    error
        using gin(data) ;
		
CREATE TABLE node (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxnode on
    node
        using gin(data) ;
		
CREATE TABLE storagearea (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxstoragearea on
    storagearea
        using gin(data) ;
        
CREATE TABLE storagepolicy (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxstoragepolicy on
    storagepolicy
        using gin(data) ;
		
CREATE TABLE documentclass (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxdocumentclass on
    documentclass
        using gin(data) ;
		
CREATE TABLE searchcriteria (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxsearchcriteria on
    idxsearchcriteria
        using gin(data) ;
		
CREATE TABLE searchtype (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxsearchtype on
    searchtype
        using gin(data) ;
		

CREATE TABLE docdata (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxdocdata on
    docdata
        using gin(data) ;
		
CREATE TABLE docversion (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxdocversion on
    docversion
        using gin(data) ;

CREATE TABLE comment (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxcomment on
    comment
        using gin(data) ;
		
CREATE TABLE likes (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxlikes on
    likes
        using gin(data) ;
		
CREATE TABLE users (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxusers on
    users
        using gin(data) ;
		
CREATE TABLE page (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxpage on
    page
        using gin(data) ;
		
CREATE TABLE pageprofile (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxpageprofile on
    pageprofile
        using gin(data) ;
		
CREATE TABLE scanprofile (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxscanprofile on
    scanprofile
        using gin(data) ;
		
CREATE TABLE storedsearch (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxstoredsearch on
    storedsearch
        using gin(data) ;
		
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
		
CREATE TABLE dococr (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxdococr on
    dococr
        using gin(data) ;
		
CREATE TABLE batch (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxbatch on
    batch
        using gin(data) ;
		
CREATE TABLE taskdef (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxtaskdef on
    taskdef
        using gin(data) ;
		
CREATE TABLE task (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxtask on
    task
        using gin(data) ;
		
CREATE TABLE workflowdef (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxworkflowdef on
    workflowdef
        using gin(data) ;
		
CREATE TABLE workflow (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxworkflow on
    workflow
        using gin(data) ;
		
		
CREATE TABLE forms (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxforms on
    forms
        using gin(data) ;

		
CREATE TABLE formsdata (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxformsdata on
    formsdata
        using gin(data) ;

		
CREATE TABLE profiles (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
CREATE INDEX idxprofiles ON profiles USING gin (data) ;

		
GRANT ALL on all tables in schema public to aquarius with grant option; 
		
INSERT INTO configuration ("data") VALUES (
'{"id": "ldap", "ldapPort": 389, "ldapUser": "cn=admin,dc=f2m,dc=com,dc=mx", "ldapServer": "localhost", "ldapPassword": "adminpass"}');
INSERT INTO configuration ("data") VALUES (
'{"id": "session", "minutes": 2, "maxUsers": 3, "maxConcurrent": 2}');
INSERT INTO configuration ("data") VALUES (
'{"id": "mail", "ttl": null, "auth": true, "host": "mail.f2m.com.mx", "port": "587", "user": "aquarius@f2m.com.mx", "sender": "aquarius@f2m.com.mx", "password": "4qU4r1U5%1"}');
INSERT INTO configuration ("data") VALUES (
'{"id": "domain", "folder": [{"name": "Home", "visible": true}, {"name": "Shared", "visible": true}, {"name": "Repository", "visible": true}, {"name": "Trash", "visible": false}, {"name": "Temp", "visible": false}, {"name": "Process", "visible": false}, {"name": "Rules", "visible": false}, {"name": "Batch", "visible": false}]}');
INSERT INTO configuration ("data") VALUES (
'{"id": "storagearea", "prefix": "sa", "defaultPath": "/opt/ecm/"}');
INSERT INTO configuration ("data") VALUES (
'{"id": "storagepolicy", "type": "domain", "prefix": "sp"}');

INSERT INTO documentclass ("data") VALUES (
'{"id": "1", "name": "document", "label": "Documento", "fields": [{"name": "title", "type": "String", "label": "Nombre", "properties": {"url": false, "size": -1, "email": false, "required": true}}]}');
INSERT INTO documentclass ("data") VALUES (
'{"id": "2", "name": "email", "label": "Correo Electrónico", "fields": [{"name": "title", "type": "String", "label": "Título", "properties": {"url": false, "size": 100, "email": false, "required": true}}, {"name": "from", "type": "String", "label": "De", "properties": {"url": false, "size": 100, "email": true, "required": true}}, {"name": "to", "type": "String", "label": "Para", "properties": {"url": false, "size": 100, "email": true, "required": true}}, {"name": "cc", "type": "String", "label": "CC", "properties": {"url": false, "size": 100, "email": true, "required": false}}, {"name": "sentOn", "type": "DateTime", "label": "Enviado el", "properties": {"required": false}}, {"name": "receivedOn", "type": "DateTime", "label": "Recibido el", "properties": {"required": false}}, {"name": "link", "type": "String", "label": "link", "properties": {"url": true, "size": -1, "email": false, "required": false}}]}');
INSERT INTO documentclass ("data") VALUES (
'{"id": "3", "name": "link", "label": "Link", "fields": [{"name": "title", "type": "String", "label": "Nombre", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "link", "type": "String", "label": "link", "properties": {"url": true, "size": -1, "email": false, "required": false}}]}');
INSERT INTO documentclass ("data") VALUES (
'{"id": "4", "name": "folder", "label": "Carpeta", "fields": [{"name": "name", "type": "String", "label": "Nombre", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "id", "type": "String", "label": "ID", "properties": {"url": false, "size": 36, "email": false, "required": true}}, {"name": "createdOn", "type": "DateTime", "label": "Creado el", "properties": {"required": false}}]}');
INSERT INTO documentclass ("data") VALUES (
'{"id": "5", "name": "fileProperties", "label": "Propiedades de Sistema Documento", "fields": [{"name": "name", "type": "String", "label": "Nombre", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "createdOn", "type": "String", "label": "Creado el", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "createdBy", "type": "String", "label": "Creado por", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "modifiedOn", "type": "String", "label": "Modificado el", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "modifiedBy", "type": "String", "label": "Modificado por", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "id", "type": "String", "label": "ID", "properties": {"url": false, "size": 36, "email": false, "required": true}}, {"name": "location", "type": "String", "label": "location", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "contentVersion", "type": "Integer", "label": "Versión Contenido", "properties": {"url": false, "size": 5, "email": false, "required": false}}, {"name": "metadataVersion", "type": "Integer", "label": "Versión Metadata", "properties": {"url": false, "size": 5, "email": false, "required": true}}, {"name": "mimeType", "type": "String", "label": "Tipo MIME", "properties": {"url": false, "size": 30, "email": false, "required": false}}, {"name": "extension", "type": "String", "label": "Extensión", "properties": {"url": false, "size": 10, "email": false, "required": false}}, {"name": "size", "type": "String", "label": "Tamaño", "properties": {"url": false, "size": 30, "email": false, "required": false}}]}');
INSERT INTO documentclass ("data") VALUES (
'{"id": "6", "name": "folderProperties", "label": "Propiedades de Sistema Carpeta", "fields": [{"name": "name", "type": "String", "label": "Nombre", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "createdOn", "type": "String", "label": "Creado el", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "createdBy", "type": "String", "label": "Creado por", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "id", "type": "String", "label": "ID", "properties": {"url": false, "size": 36, "email": false, "required": true}}, {"name": "location", "type": "String", "label": "location", "properties": {"url": false, "size": -1, "email": false, "required": true}}, {"name": "numDocuments", "type": "Integer", "label": "Número Documentos", "properties": {"url": false, "size": 5, "email": false, "required": true}}, {"name": "numFolders", "type": "Integer", "label": "Número Carpetas", "properties": {"url": false, "size": 5, "email": false, "required": true}}]}');

INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Igual", "search_criteria": "Equals"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "No es igual", "search_criteria": "NotEquals"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Empieza Por", "search_criteria": "StartWith"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Finaliza Con", "search_criteria": "EndWith"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Como", "search_criteria": "Like"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "No es como", "search_criteria": "NotLike"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Menor que", "search_criteria": "LessThan"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Menor o igual que", "search_criteria": "LessThanOrEqual"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Mayor que", "search_criteria": "GreaterThan"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Mayor o igual que", "search_criteria": "GreaterThanOrEqual"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Incluir cualquiera", "search_criteria": "IncludeAny"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Excluir todo", "search_criteria": "ExcludeAll"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Esta vacío", "search_criteria": "IsEmpty"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "No está vacío", "search_criteria": "IsNotEmpty"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "Entre", "search_criteria": "Between"}');
INSERT INTO searchcriteria ("data") VALUES (
'{"label": "No entre", "search_criteria": "NotBetween"}');


INSERT INTO searchtype ("data") VALUES (
'{"type": "String", "search_criteria": ["StartWith", "EndWith", "Like", "NotLike", "Equals", "NotEquals", "IncludeAny", "ExcludeAll", "IsEmpty", "IsNotEmpty"]}');
INSERT INTO searchtype ("data") VALUES (
'{"type": "Text", "search_criteria": ["StartWith", "EndWith", "Like", "NotLike", "Equals", "NotEquals", "IncludeAny", "ExcludeAll", "IsEmpty", "IsNotEmpty"]}');
INSERT INTO searchtype ("data") VALUES (
'{"type": "Integer", "search_criteria": ["Equals", "NotEquals", "LessThan", "LessThanOrEqual", "GreaterThan", "GreaterThanOrEqual", "IsEmpty", "IsNotEmpty", "Between", "NotBetween"]}');
INSERT INTO searchtype ("data") VALUES (
'{"type": "Numeric", "search_criteria": ["Equals", "NotEquals", "LessThan", "LessThanOrEqual", "GreaterThan", "GreaterThanOrEqual", "IsEmpty", "IsNotEmpty", "Between", "NotBetween"]}');
INSERT INTO searchtype ("data") VALUES (
'{"type": "Long", "search_criteria": ["Equals", "NotEquals", "LessThan", "LessThanOrEqual", "GreaterThan", "GreaterThanOrEqual", "IsEmpty", "IsNotEmpty", "Between", "NotBetween"]}');
INSERT INTO searchtype ("data") VALUES (
'{"type": "Decimal", "search_criteria": ["Equals", "NotEquals", "LessThan", "LessThanOrEqual", "GreaterThan", "GreaterThanOrEqual", "IsEmpty", "IsNotEmpty", "Between", "NotBetween"]}');
INSERT INTO searchtype ("data") VALUES (
'{"type": "Currency", "search_criteria": ["Equals", "NotEquals", "LessThan", "LessThanOrEqual", "GreaterThan", "GreaterThanOrEqual", "IsEmpty", "IsNotEmpty", "Between", "NotBetween"]}');
INSERT INTO searchtype ("data") VALUES (
'{"type": "Date", "search_criteria": ["Equals", "LessThan", "LessThanOrEqual", "GreaterThan", "GreaterThanOrEqual", "IsEmpty", "IsNotEmpty", "Between", "NotBetween"]}');
INSERT INTO searchtype ("data") VALUES (
'{"type": "DateTime", "search_criteria": ["Equals", "LessThan", "LessThanOrEqual", "GreaterThan", "GreaterThanOrEqual", "IsEmpty", "IsNotEmpty", "Between", "NotBetween"]}');
INSERT INTO searchtype ("data") VALUES (
'{"type": "List", "search_criteria": ["Equals", "NotEquals", "IsEmpty", "IsNotEmpty"]}');
INSERT INTO searchtype ("data") VALUES (
'{"type": "Boolean", "search_criteria": ["Equals", "NotEquals", "IsEmpty", "IsNotEmpty"]}');

INSERT INTO page ("data") VALUES (
'{"id": "auditoria", "name": "Auditoria", "createdBy": "ECM", "createdOn": {"time": 1523640215124, "timezone": "Etc/UTC"}, "components": []}');
INSERT INTO page ("data") VALUES (
'{"id": "monitoreo", "name": "Monitoreo", "createdBy": "ECM", "createdOn": {"time": 1523640209597, "timezone": "Etc/UTC"}, "components": []}');
INSERT INTO page ("data") VALUES (
'{"id": "reportes", "name": "Reportes", "createdBy": "ECM", "createdOn": {"time": 1523640204955, "timezone": "Etc/UTC"}, "components": []}');
INSERT INTO page ("data") VALUES (
'{"id": "captura", "name": "Captura", "createdBy": "ECM", "createdOn": {"time": 1523640175303, "timezone": "Etc/UTC"}, "components": []}');
INSERT INTO page ("data") VALUES (
'{"id": "portal", "name": "Portal", "createdBy": "ECM", "createdOn": {"time": 1523554521202, "timezone": "Etc/UTC"}, "components": []}');
INSERT INTO page ("data") VALUES (
'{"id": "explorar", "name": "Explorar", "createdBy": "ECM", "createdOn": {"time": 1523640181971, "timezone": "Etc/UTC"}, "components": [{"id": "explorar.comentarios", "name": "Comentarios"}, {"id": "explorar.propiedadesdedocumento", "name": "Propiedades de documento"}, {"id": "explorar.visordeimagen", "name": "Visor de imagen"}, {"id": "explorar.editorpdf", "name": "Editor pdf"}, {"id": "explorar.visorpdf", "name": "Visor pdf"}, {"id": "explorar.reacciones", "name": "Reacciones"}, {"id": "explorar.propiedadessistema", "name": "Propiedades sistema"}, {"id": "explorar.visortiff", "name": "Visor tiff"}, {"id": "explorar.historialversiones", "name": "Historial versiones"}, {"id": "explorar.reproductorvideo", "name": "Reproductor video"}]}');
INSERT INTO page ("data") VALUES (
'{"id": "busqueda", "name": "Busqueda", "createdBy": "ECM", "createdOn": {"time": 1523640189655, "timezone": "Etc/UTC"}, "components": [{"id": "busqueda.criteriosbusqueda", "name": "Criterios busqueda"}, {"id": "busqueda.tipobusqueda", "name": "Tipo busqueda"}, {"id": "busqueda.visualizacionresultados", "name": "Visualizacion resultados"}, {"id": "busqueda.nuevabusqueda", "name": "Nueva busqueda"}]}');
INSERT INTO page ("data") VALUES (
'{"id": "tareas", "name": "Tareas", "createdBy": "ECM", "createdOn": {"time": 1523640196463, "timezone": "Etc/UTC"}, "components": [{"id": "tareas.paneltarea", "name": "Panel tarea"}]}');

INSERT INTO pageprofile ("data") VALUES (
'{"id": "default", "name": "Default", "elements": [{"pageId": "tareas", "componentsId": ["tareas.paneltarea"]}, {"pageId": "busqueda", "componentsId": ["busqueda.criteriosbusqueda", "busqueda.tipobusqueda", "busqueda.visualizacionresultados", "busqueda.nuevabusqueda"]}, {"pageId": "explorar", "componentsId": ["explorar.comentarios", "explorar.propiedadesdedocumento", "explorar.visordeimagen", "explorar.editorpdf", "explorar.visorpdf", "explorar.reacciones", "explorar.propiedadessistema", "explorar.visortiff", "explorar.historialversiones", "explorar.reproductorvideo"]}, {"pageId": "captura", "componentsId": []}, {"pageId": "portal", "componentsId": []}], "createdBy": "ECM", "createdOn": {"time": 1523902947820, "timezone": "America/Mexico_City"}}');

INSERT INTO stamp ("data") VALUES (
'{"id": "default_aceptado", "name": "Default Aceptado", "size": 34267, "mimeType": "image/png", "createdBy": "ecm", "createdOn": {"time": 1526748896988, "timezone": "America/Mexico_City"}, "extension": ""}');
INSERT INTO stamp ("data") VALUES (
'{"id": "default_pagado", "name": "Default Pagado", "size": 27067, "mimeType": "image/png", "createdBy": "ecm", "createdOn": {"time": 1526748928650, "timezone": "America/Mexico_City"}, "extension": ""}');
INSERT INTO stamp ("data") VALUES (
'{"id": "default_rechazado", "name": "Default Rechazado", "size": 35044, "mimeType": "image/png", "createdBy": "ecm", "createdOn": {"time": 1526748946931, "timezone": "America/Mexico_City"}, "extension": ""}');
INSERT INTO stamp ("data") VALUES (
'{"id": "default_recibido", "name": "Default Recibido", "size": 29557, "mimeType": "image/png", "createdBy": "ecm", "createdOn": {"time": 1526748961459, "timezone": "America/Mexico_City"}, "extension": ""}');

INSERT INTO error ("data") VALUES ('{"id": -1, "locale": "es", "message": "Error desconocido"}');
INSERT INTO error ("data") VALUES ('{"id": 0, "locale": "es", "message": "Operación Exitosa"}');
INSERT INTO error ("data") VALUES ('{"id": 1, "locale": "es", "message": "La sesión ha expirado"}');
INSERT INTO error ("data") VALUES ('{"id": 2, "locale": "es", "message": "Sesión duplicada"}');
INSERT INTO error ("data") VALUES ('{"id": 3, "locale": "es", "message": "Credenciales invalidas"}');
INSERT INTO error ("data") VALUES ('{"id": 4, "locale": "es", "message": "No se encontró la sesión"}');
INSERT INTO error ("data") VALUES ('{"id": 5, "locale": "es", "message": "Información de sesión incompleta"}');
INSERT INTO error ("data") VALUES ('{"id": 6, "locale": "es", "message": "Sesión no valida"}');
INSERT INTO error ("data") VALUES ('{"id": 7, "locale": "es", "message": "El usuario ha excedido el número de sesiones concurrentes"}');
INSERT INTO error ("data") VALUES ('{"id": 8, "locale": "es", "message": "No se pudo cerrar la sesión"}');
INSERT INTO error ("data") VALUES ('{"id": 9, "locale": "es", "message": "No se pudo iniciar la sesión"}');
INSERT INTO error ("data") VALUES ('{"id": 10, "locale": "es", "message": "El número de usuarios excede el máximo permitido"}');
INSERT INTO error ("data") VALUES ('{"id": 101, "locale": "es", "message": "No se encontró al usuario con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 102, "locale": "es", "message": "Hay más de un usuario con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 103, "locale": "es", "message": "La contraseña no cumple con los requisitos"}');
INSERT INTO error ("data") VALUES ('{"id": 104, "locale": "es", "message": "Error al codificar la contraseña del usuario"}');
INSERT INTO error ("data") VALUES ('{"id": 105, "locale": "es", "message": "El usuario no tiene los datos necesarios para verificar la contraseña"}');
INSERT INTO error ("data") VALUES ('{"id": 106, "locale": "es", "message": "Error al verificar la contraseña del usuario"}');
INSERT INTO error ("data") VALUES ('{"id": 107, "locale": "es", "message": "El usuario no tiene los datos necesarios para actualizar la contraseña"}');
INSERT INTO error ("data") VALUES ('{"id": 108, "locale": "es", "message": "Error al intentar actualizar la contraseña del usuario"}');
INSERT INTO error ("data") VALUES ('{"id": 109, "locale": "es", "message": "El DN del usuario no puede ser nulo o estar vacío"}');
INSERT INTO error ("data") VALUES ('{"id": 110, "locale": "es", "message": "Error al intentar eliminar el usuario"}');
INSERT INTO error ("data") VALUES ('{"id": 111, "locale": "es", "message": "El query no puede ser nulo o estar vacío"}');
INSERT INTO error ("data") VALUES ('{"id": 112, "locale": "es", "message": "Error al realizar la búsqueda con filtro"}');
INSERT INTO error ("data") VALUES ('{"id": 113, "locale": "es", "message": "El Dominio del usuario no puede ser nulo o estar vacío"}');
INSERT INTO error ("data") VALUES ('{"id": 114, "locale": "es", "message": "El UID del usuario no puede ser nulo o estar vacío"}');
INSERT INTO error ("data") VALUES ('{"id": 115, "locale": "es", "message": "Error al listar los usuarios"}');
INSERT INTO error ("data") VALUES ('{"id": 116, "locale": "es", "message": "El usuario no tiene los datos necesarios para insertarlo"}');
INSERT INTO error ("data") VALUES ('{"id": 117, "locale": "es", "message": "Error al intentar insertar el usuario"}');
INSERT INTO error ("data") VALUES ('{"id": 118, "locale": "es", "message": "No se pudo eliminar el usuario"}');
INSERT INTO error ("data") VALUES ('{"id": 119, "locale": "es", "message": "No se pudo insertar o actualizar el usuario"}');
INSERT INTO error ("data") VALUES ('{"id": 120, "locale": "es", "message": "El usuario no tiene los datos necesarios para realizar la búsqueda"}');
INSERT INTO error ("data") VALUES ('{"id": 121, "locale": "es", "message": "El usuario no existe en la Base de Datos"}');
INSERT INTO error ("data") VALUES ('{"id": 201, "locale": "es", "message": "No se encontró el grupo con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 202, "locale": "es", "message": "Hay más de un grupo con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 203, "locale": "es", "message": "El DN del grupo no puede ser nulo o estar vacío"}');
INSERT INTO error ("data") VALUES ('{"id": 204, "locale": "es", "message": "Error al intentar eliminar el grupo"}');
INSERT INTO error ("data") VALUES ('{"id": 205, "locale": "es", "message": "Error al listar los grupos"}');
INSERT INTO error ("data") VALUES ('{"id": 206, "locale": "es", "message": "El grupo no tiene los datos necesarios para insertarlo"}');
INSERT INTO error ("data") VALUES ('{"id": 207, "locale": "es", "message": "Error al insertar el grupo"}');
INSERT INTO error ("data") VALUES ('{"id": 208, "locale": "es", "message": "El query no puede ser nulo o estar vacío"}');
INSERT INTO error ("data") VALUES ('{"id": 209, "locale": "es", "message": "Error al realizar la búsqueda con filtro"}');
INSERT INTO error ("data") VALUES ('{"id": 210, "locale": "es", "message": "El Dominio del grupo no puede ser nulo o estar vacío"}');
INSERT INTO error ("data") VALUES ('{"id": 211, "locale": "es", "message": "El CN del grupo no puede ser nulo o estar vacío"}');
INSERT INTO error ("data") VALUES ('{"id": 212, "locale": "es", "message": "Error al buscar los grupos del usuario"}');
INSERT INTO error ("data") VALUES ('{"id": 213, "locale": "es", "message": "Error al buscar los miembros del grupo"}');
INSERT INTO error ("data") VALUES ('{"id": 214, "locale": "es", "message": "El usuario y/o grupos no pueden ser nulo"}');
INSERT INTO error ("data") VALUES ('{"id": 215, "locale": "es", "message": "No se pudo eliminar el grupo"}');
INSERT INTO error ("data") VALUES ('{"id": 216, "locale": "es", "message": "No se pudo insertar o actualizar el grupo"}');
INSERT INTO error ("data") VALUES ('{"id": 301, "locale": "es", "message": "La OU no tiene los datos necesarios para insertarla"}');
INSERT INTO error ("data") VALUES ('{"id": 302, "locale": "es", "message": "Error al insertar la Organization Unit"}');
INSERT INTO error ("data") VALUES ('{"id": 303, "locale": "es", "message": "La OU no tiene los datos necesarios para eliminarla"}');
INSERT INTO error ("data") VALUES ('{"id": 304, "locale": "es", "message": "Error al eliminar la Organization Unit"}');
INSERT INTO error ("data") VALUES ('{"id": 305, "locale": "es", "message": "El Base DN no puede ser nulo o estar vacío"}');
INSERT INTO error ("data") VALUES ('{"id": 306, "locale": "es", "message": "Error al listar los elementos"}');
INSERT INTO error ("data") VALUES ('{"id": 307, "locale": "es", "message": "No se pudo insertar la Organization Unit"}');
INSERT INTO error ("data") VALUES ('{"id": 308, "locale": "es", "message": "No se pudo eliminar la Organization Unit"}');
INSERT INTO error ("data") VALUES ('{"id": 401, "locale": "es", "message": "No se encontró el objeto"}');
INSERT INTO error ("data") VALUES ('{"id": 402, "locale": "es", "message": "Error al obtener el objeto"}');
INSERT INTO error ("data") VALUES ('{"id": 403, "locale": "es", "message": "No se encontraron los objetos con la información proporcionada"}');
INSERT INTO error ("data") VALUES ('{"id": 404, "locale": "es", "message": "El objeto no tiene los datos necesarios para insertarlo"}');
INSERT INTO error ("data") VALUES ('{"id": 405, "locale": "es", "message": "No se puede crear el objeto. Ya existe un objeto con el mismo nombre"}');
INSERT INTO error ("data") VALUES ('{"id": 406, "locale": "es", "message": "El contenido del documento es nulo o viene vacío"}');
INSERT INTO error ("data") VALUES ('{"id": 407, "locale": "es", "message": "Error al guardar el archivo"}');
INSERT INTO error ("data") VALUES ('{"id": 408, "locale": "es", "message": "El directorio raíz en el File System no existe o no está vacío"}');
INSERT INTO error ("data") VALUES ('{"id": 409, "locale": "es", "message": "No se tienen los datos necesarios para el directorio raíz en el File System"}');
INSERT INTO error ("data") VALUES ('{"id": 410, "locale": "es", "message": "Error al crear el Storage Area"}');
INSERT INTO error ("data") VALUES ('{"id": 411, "locale": "es", "message": "Los datos del objeto a crear no corresponden con los datos de su padre"}');
INSERT INTO error ("data") VALUES ('{"id": 412, "locale": "es", "message": "No se encontró el dominio"}');
INSERT INTO error ("data") VALUES ('{"id": 413, "locale": "es", "message": "No se encontró el Storage Area"}');
INSERT INTO error ("data") VALUES ('{"id": 414, "locale": "es", "message": "Error al crear la Storage Policy"}');
INSERT INTO error ("data") VALUES ('{"id": 415, "locale": "es", "message": "Error al crear el Documento"}');
INSERT INTO error ("data") VALUES ('{"id": 416, "locale": "es", "message": "El contenido del documento excede el tamaño máximo permitido"}');
INSERT INTO error ("data") VALUES ('{"id": 417, "locale": "es", "message": "No se encontró la Storage Policy"}');
INSERT INTO error ("data") VALUES ('{"id": 418, "locale": "es", "message": "No se encontró la información para recuperar el contenido del documento"}');
INSERT INTO error ("data") VALUES ('{"id": 419, "locale": "es", "message": "Error al guardar la información de la versión del documento"}');
INSERT INTO error ("data") VALUES ('{"id": 420, "locale": "es", "message": "Error al guardar la información de la metadata del documento"}');
INSERT INTO error ("data") VALUES ('{"id": 421, "locale": "es", "message": "Error al actualizar el status de las versiones anteriores del documento"}');
INSERT INTO error ("data") VALUES ('{"id": 422, "locale": "es", "message": "El objeto no tiene los datos necesarios para agregarlo a favoritos"}');
INSERT INTO error ("data") VALUES ('{"id": 423, "locale": "es", "message": "No se encontraron favoritos con la información proporcionada"}');
INSERT INTO error ("data") VALUES ('{"id": 424, "locale": "es", "message": "No se tiene la información necesaria para mover la carpeta"}');
INSERT INTO error ("data") VALUES ('{"id": 425, "locale": "es", "message": "No se tiene la información necesaria para copiar la carpeta"}');
INSERT INTO error ("data") VALUES ('{"id": 426, "locale": "es", "message": "No se tiene la información necesaria para descargar la carpeta"}');
INSERT INTO error ("data") VALUES ('{"id": 427, "locale": "es", "message": "No se definió el tipo de descarga de la carpeta"}');
INSERT INTO error ("data") VALUES ('{"id": 428, "locale": "es", "message": "Error en la descarga de la carpeta"}');
INSERT INTO error ("data") VALUES ('{"id": 429, "locale": "es", "message": "No se encontraron plantillas con la información proporcionada"}');
INSERT INTO error ("data") VALUES ('{"id": 430, "locale": "es", "message": "El objeto no tiene los datos necesarios para agregarlo a una plantilla"}');
INSERT INTO error ("data") VALUES ('{"id": 431, "locale": "es", "message": "Error al ejecutar la plantilla"}');
INSERT INTO error ("data") VALUES ('{"id": 432, "locale": "es", "message": "No se tiene la información necesaria para eliminar la plantilla"}');
INSERT INTO error ("data") VALUES ('{"id": 433, "locale": "es", "message": "No se tiene la información necesaria para eliminar la carpeta"}');
INSERT INTO error ("data") VALUES ('{"id": 434, "locale": "es", "message": "El objeto no tiene los datos necesarios para eliminarlo de favoritos"}');
INSERT INTO error ("data") VALUES ('{"id": 435, "locale": "es", "message": "No se tiene la información necesaria para mover el documento"}');
INSERT INTO error ("data") VALUES ('{"id": 436, "locale": "es", "message": "No se tiene la información necesaria para copiar el documento"}');
INSERT INTO error ("data") VALUES ('{"id": 437, "locale": "es", "message": "No se tiene la información necesaria para eliminar el documento"}');
INSERT INTO error ("data") VALUES ('{"id": 438, "locale": "es", "message": "No se definió el tipo de descarga del documento"}');
INSERT INTO error ("data") VALUES ('{"id": 439, "locale": "es", "message": "No se tiene la información necesaria para compartir el documento"}');
INSERT INTO error ("data") VALUES ('{"id": 440, "locale": "es", "message": "No se tiene la información necesaria para asignar versión actual al documento"}');
INSERT INTO error ("data") VALUES ('{"id": 441, "locale": "es", "message": "No se tiene la información necesaria para reservar el documento"}');
INSERT INTO error ("data") VALUES ('{"id": 442, "locale": "es", "message": "No se tiene la información necesaria para cancelar la reserva del documento"}');
INSERT INTO error ("data") VALUES ('{"id": 443, "locale": "es", "message": "No se tiene la información necesaria para la  nueva versión del documento"}');
INSERT INTO error ("data") VALUES ('{"id": 444, "locale": "es", "message": "No se tiene la información para actualizar el nombre del objeto"}');
INSERT INTO error ("data") VALUES ('{"id": 445, "locale": "es", "message": "No se tiene la información para actualizar la visibilidad del objeto"}');
INSERT INTO error ("data") VALUES ('{"id": 446, "locale": "es", "message": "No se tiene la información necesaria para obtener los dominios"}');
INSERT INTO error ("data") VALUES ('{"id": 447, "locale": "es", "message": "No se tiene la información necesaria para obtener las Storage Area"}');
INSERT INTO error ("data") VALUES ('{"id": 448, "locale": "es", "message": "No se tiene la información necesaria para obtener las Storage Policy"}');
INSERT INTO error ("data") VALUES ('{"id": 449, "locale": "es", "message": "No se tiene la información necesaria para agregar un Sello"}');
INSERT INTO error ("data") VALUES ('{"id": 450, "locale": "es", "message": "No se tiene la información necesaria para obtener un Sello"}');
INSERT INTO error ("data") VALUES ('{"id": 451, "locale": "es", "message": "Error al guardar la información del Sello"}');
INSERT INTO error ("data") VALUES ('{"id": 452, "locale": "es", "message": "No se tiene la información necesaria para eliminar el sello"}');
INSERT INTO error ("data") VALUES ('{"id": 501, "locale": "es", "message": "Error de conexión con la Base de Datos"}');
INSERT INTO error ("data") VALUES ('{"id": 502, "locale": "es", "message": "No se tiene la información completa para generar el query"}');
INSERT INTO error ("data") VALUES ('{"id": 503, "locale": "es", "message": "Error al ejecutar el query"}');
INSERT INTO error ("data") VALUES ('{"id": 504, "locale": "es", "message": "Error al leer el objeto Json en la Base de Datos"}');
INSERT INTO error ("data") VALUES ('{"id": 601, "locale": "es", "message": "Error de conexión con el servidor de Correo Electrónico"}');
INSERT INTO error ("data") VALUES ('{"id": 602, "locale": "es", "message": "Error al enviar un Correo Electrónico"}');
INSERT INTO error ("data") VALUES ('{"id": 701, "locale": "es", "message": "No se encontró información con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 702, "locale": "es", "message": "No se encuentra la información solicitada"}');
INSERT INTO error ("data") VALUES ('{"id": 801, "locale": "es", "message": "No se encontró los parametros de la configuración solicitada"}');
INSERT INTO error ("data") VALUES ('{"id": 802, "locale": "es", "message": "No se pudo cargar la configuración"}');
INSERT INTO error ("data") VALUES ('{"id": 901, "locale": "es", "message": "No se encontró el comentario con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 902, "locale": "es", "message": "El objeto no tiene los datos necesarios para insertarlo"}');
INSERT INTO error ("data") VALUES ('{"id": 903, "locale": "es", "message": "Los componentes sociales sólo pueden ser aplicados a objetos de tipo Document"}');
INSERT INTO error ("data") VALUES ('{"id": 904, "locale": "es", "message": "Error al crear el comentario"}');
INSERT INTO error ("data") VALUES ('{"id": 905, "locale": "es", "message": "Error al consultar los comentarios"}');
INSERT INTO error ("data") VALUES ('{"id": 906, "locale": "es", "message": "No se encontró el like con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 907, "locale": "es", "message": "Error al guardar el Like"}');
INSERT INTO error ("data") VALUES ('{"id": 1001, "locale": "es", "message": "No se encontró el perfil con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 1002, "locale": "es", "message": "El objeto no tiene los datos necesarios para insertarlo"}');
INSERT INTO error ("data") VALUES ('{"id": 1003, "locale": "es", "message": "Error al crear el profile"}');
INSERT INTO error ("data") VALUES ('{"id": 1004, "locale": "es", "message": "Error al consultar los perfiles"}');
INSERT INTO error ("data") VALUES ('{"id": 1005, "locale": "es", "message": "No se puede eliminar el perfil con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 1101, "locale": "es", "message": "No se encontró la página con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 1102, "locale": "es", "message": "El objeto no tiene los datos necesarios para insertarlo"}');
INSERT INTO error ("data") VALUES ('{"id": 1103, "locale": "es", "message": "Error al insertar la página"}');
INSERT INTO error ("data") VALUES ('{"id": 1104, "locale": "es", "message": "Error al insertar el componente"}');
INSERT INTO error ("data") VALUES ('{"id": 1105, "locale": "es", "message": "Error al insertar el perfil de página"}');
INSERT INTO error ("data") VALUES ('{"id": 1106, "locale": "es", "message": "No se encontró el componente con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 1107, "locale": "es", "message": "No se encontró el perfil con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 1201, "locale": "es", "message": "No se encontró la búsqueda almacenada con los datos proporcionados"}');
INSERT INTO error ("data") VALUES ('{"id": 1202, "locale": "es", "message": "El objeto no tiene los datos necesarios para insertarlo"}');
INSERT INTO error ("data") VALUES ('{"id": 1203, "locale": "es", "message": "Error al insertar la búsqueda almacenada"}');
INSERT INTO error ("data") VALUES ('{"id": 1204, "locale": "es", "message": "El objeto no tiene los datos necesarios para ejecutar la búsqueda"}');
INSERT INTO error ("data") VALUES ('{"id": 1301, "locale": "es", "message": "Error al cambiar el tiempo de ejecución del Batch. No se encuentra en el rango permitido"}');
INSERT INTO error ("data") VALUES ('{"id": 1302, "locale": "es", "message": "No se tienen la información necesaria para cambiar el tiempo de ejecución del Batch"}');
INSERT INTO error ("data") VALUES ('{"id": 1303, "locale": "es", "message": "No se tiene la información necesaria para iniciar/detener la ejecución del Batch"}');
INSERT INTO error ("data") VALUES ('{"id": 1304, "locale": "es", "message": "Error al consultar los procesos batch"}');
INSERT INTO error ("data") VALUES ('{"id": 1305, "locale": "es", "message": "No se tiene la información necesaria para insertar un proceso batch"}');
INSERT INTO error ("data") VALUES ('{"id": 1306, "locale": "es", "message": "Error al insertar el proceso batch"}');
INSERT INTO error ("data") VALUES ('{"id": 1307, "locale": "es", "message": "No se tiene la información necesaria para eliminar un proceso batch"}');
INSERT INTO error ("data") VALUES ('{"id": 1308, "locale": "es", "message": "No se tiene la información necesaria para la ejecución del OCR"}');
INSERT INTO error ("data") VALUES ('{"id": 1309, "locale": "es", "message": "No se encontró el archivo OCR"}');
INSERT INTO error ("data") VALUES ('{"id": 1310, "locale": "es", "message": "Error al ejecutar el motor de OCR"}');
INSERT INTO error ("data") VALUES ('{"id": 1311, "locale": "es", "message": "No se tiene la información necesaria para eliminar el OCR de un documento"}');
INSERT INTO error ("data") VALUES ('{"id": 1312, "locale": "es", "message": "No se tiene la información necesaria para procesar una tarea de OCR"}');
INSERT INTO error ("data") VALUES ('{"id": 1313, "locale": "es", "message": "No se tiene la información necesaria para insertar un proceso OCR"}');
INSERT INTO error ("data") VALUES ('{"id": 1314, "locale": "es", "message": "Error al intentar leer el archivo en formato PDF"}');
INSERT INTO error ("data") VALUES ('{"id": 1315, "locale": "es", "message": "Error al guardar el archivo para el reconocimiento del OCR"}');
;