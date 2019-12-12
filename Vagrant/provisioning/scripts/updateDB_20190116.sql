drop table task;

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

GRANT ALL on all tables in schema public to aquarius with grant option; 
