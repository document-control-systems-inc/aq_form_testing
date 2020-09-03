drop table page;
drop table pageProfile;
drop table storedsearch;

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


CREATE TABLE pageProfile (
	"data" jsonb NULL
)
WITH (
	OIDS=FALSE
) ;
create
    index idxpageProfile on
    pageProfile
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
		
GRANT ALL on all tables in schema public to aquarius with grant option; 




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

