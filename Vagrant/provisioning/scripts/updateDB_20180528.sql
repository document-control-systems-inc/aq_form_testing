drop table batch;

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

GRANT ALL on all tables in schema public to aquarius with grant option; 
