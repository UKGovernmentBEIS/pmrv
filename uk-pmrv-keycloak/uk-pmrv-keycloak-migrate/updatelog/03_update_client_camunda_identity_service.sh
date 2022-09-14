#!/bin/bash

#This script updates realm's client with id 9d233e9a-ed07-4e83-b572-fd97aede8bdb

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
UPDATE_REALM_CLIENT_URL="$BASE_URL/admin/realms/master/clients/9d233e9a-ed07-4e83-b572-fd97aede8bdb"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

#Create new realm
UPDATE_REALM_CLIENT=$(curl -s -L -X PUT "$UPDATE_REALM_CLIENT_URL" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{
	"redirectUris" : [ "'$PMRV_ADMIN_APP_URL'/*" ],
	"webOrigins": ["'$PMRV_ADMIN_APP_URL'"],
}')

if [ -z "$UPDATE_REALM_CLIENT" ]
then
	echo " $SCRIPT_NAME executed successfully"
else
	echo " $SCRIPT_NAME failed $UPDATE_REALM_CLIENT"
fi
