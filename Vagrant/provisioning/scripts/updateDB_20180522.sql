drop table scanprofile;

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

GRANT ALL on all tables in schema public to aquarius with grant option; 

INSERT INTO scanprofile ("data") VALUES ('{"id": "4f03a7ca-6bba-4f71-89af-a7e53a12ea23", "user": "DEVELOPER", "profile": {"bit": "8", "align": "center", "delay": "0", "paper": "letter", "scale": "100%", "white": "0", "device": "device01", "perfil": "perfil1", "quality": "100", "contrast": "0", "coverage": "0", "brightness": "100", "drivername": "Test01", "resolution": "200ppi"}, "createdBy": "DEVELOPER", "createdOn": {"time": 1524073714217, "timezone": "Etc/UTC"}}');
