sudo pg_dump -h localhost -p 5432 -U postgres -F c -b -v -f "$1" aquarius
