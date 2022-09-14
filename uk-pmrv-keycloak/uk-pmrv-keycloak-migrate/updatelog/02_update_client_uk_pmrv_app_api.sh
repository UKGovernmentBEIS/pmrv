#!/bin/bash

#This script updates realm's client with id a2d1abe6-f362-422a-a17a-e0a2d566a265

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
UPDATE_REALM_CLIENT_URL="$BASE_URL/admin/realms/$UK_PMRV_REALM_NAME/clients/a2d1abe6-f362-422a-a17a-e0a2d566a265"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

#Create new realm
UPDATE_REALM_CLIENT=$(curl -s -L -X PUT "$UPDATE_REALM_CLIENT_URL" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{
	"redirectUris" : [ "'$PMRV_API_APP_URL'" ],
	"webOrigins": ["'$PMRV_API_APP_URL'", "'$PMRV_WEB_APP_URL'"],
	"secret" : "'$PMRV_APP_API_CLIENT_SECRET'"
}')

if [ -z "$UPDATE_REALM_CLIENT" ]
then
	echo " $SCRIPT_NAME executed successfully"
else
	echo " $SCRIPT_NAME failed $UPDATE_REALM_CLIENT"
fi
