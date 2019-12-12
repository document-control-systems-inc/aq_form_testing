drop table dococr;

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

GRANT ALL on all tables in schema public to aquarius with grant option; 
