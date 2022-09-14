#!/bin/bash

# Define initial logging format
sed -i -E "s/(handler.CONSOLE.formatter=)COLOR-PATTERN/\1\PATTERN/" /opt/jboss/keycloak/standalone/configuration/logging.properties

CURRENT_PATH="$(dirname "$0")"
source "$CURRENT_PATH/migrations/migrate_keycloak_vars.sh"

/opt/jboss/tools/docker-entrypoint.sh -b 0.0.0.0 &
until $(curl --output /dev/null --silent --head --fail $BASE_URL/realms/master); do
    sleep 3
done
/opt/jboss/tools/migrations/migrate_keycloak.sh
sleep infinity
