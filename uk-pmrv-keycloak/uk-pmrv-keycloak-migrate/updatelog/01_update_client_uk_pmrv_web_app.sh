#!/bin/bash

#This script updates realm's client with id 4beee482-515a-4cd5-b835-14c781a7c8d7

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
UPDATE_REALM_CLIENT_URL="$BASE_URL/admin/realms/$UK_PMRV_REALM_NAME/clients/4beee482-515a-4cd5-b835-14c781a7c8d7"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

#Create new realm
UPDATE_REALM_CLIENT=$(curl -s -L -X PUT "$UPDATE_REALM_CLIENT_URL" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{
	"redirectUris": ["'$PMRV_WEB_APP_URL'/*"],
	"baseUrl": "'$PMRV_WEB_APP_URL'",
	"adminUrl": "'$PMRV_WEB_APP_URL'",
	"webOrigins": ["'$PMRV_WEB_APP_URL'"]
}')

if [ -z "$UPDATE_REALM_CLIENT" ]
then
	echo " $SCRIPT_NAME executed successfully"
else
	echo " $SCRIPT_NAME failed $UPDATE_REALM_CLIENT"
fi
