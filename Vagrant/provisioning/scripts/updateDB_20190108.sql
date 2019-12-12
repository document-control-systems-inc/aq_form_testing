drop table taskdef;

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

GRANT ALL on all tables in schema public to aquarius with grant option; 
