sudo pg_restore -h localhost -p 5432 -U postgres -d aquarius -v "$1"
