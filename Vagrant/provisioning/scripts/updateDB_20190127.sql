drop table workflow;

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

GRANT ALL on all tables in schema public to aquarius with grant option; 
