drop table forms;

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

drop table formsdata;

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
		
GRANT ALL on all tables in schema public to aquarius with grant option; 
