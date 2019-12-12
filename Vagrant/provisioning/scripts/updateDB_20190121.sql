drop table workflowdef;

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

GRANT ALL on all tables in schema public to aquarius with grant option; 
