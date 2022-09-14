#!/bin/bash

set -e
export PGPASSWORD=password

  psql -U pmrv -h localhost -p 5433 -d pmrv -c "insert into terms values (-1, '/', 0) ON CONFLICT (id) DO NOTHING;"

