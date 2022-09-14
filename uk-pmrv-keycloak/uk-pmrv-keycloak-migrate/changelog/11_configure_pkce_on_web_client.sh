#!/bin/bash

#This script updates the uk-pmrv realm in order to configure Session and Token Timeouts.

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
UPDATE_WEB_CLIENT_URL="$BASE_URL/admin/realms/$UK_PMRV_REALM_NAME/clients/4beee482-515a-4cd5-b835-14c781a7c8d7"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

#Configures the uk-pmrv web client
CONFIG_UK_PMRV_WEB_CLIENT=$(curl -s -L -X PUT "$UPDATE_WEB_CLIENT_URL" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{
			"id": "4beee482-515a-4cd5-b835-14c781a7c8d7",
			"clientId": "uk-pmrv-web-app",
			"enabled": true,
			"publicClient" : true,
			"redirectUris": ["'$PMRV_WEB_APP_URL'/*"],
			"protocol": "openid-connect",
			"attributes": {},
			"baseUrl": "'$PMRV_WEB_APP_URL'",
			"adminUrl": "'$PMRV_WEB_APP_URL'",
			"webOrigins": ["'$PMRV_WEB_APP_URL'"],
			"directAccessGrantsEnabled": true,
			"attributes": {
        "pkce.code.challenge.method": "S256"
      }
}')

if [ -z "$CONFIG_UK_PMRV_WEB_CLIENT" ]
then
	echo "Realm $UK_PMRV_REALM_NAME updated successfully"
else
	#In case of error during realm creation, print the error and exit in order to avoid successfully logging the script execution
	echo "$CONFIG_UK_PMRV_WEB_CLIENT"
	exit;
fi

#Add script name as user to changelog realm for tracking purposes
ADD_SCRIPT_TO_CHANGELOG=$(addUserToChangeLogRealm "$SCRIPT_NAME")

if [ -z "$ADD_SCRIPT_TO_CHANGELOG" ]
then
	echo " Script $SCRIPT_NAME added to changelog"
else
	echo "Script $SCRIPT_NAME was not to added to changelog. Reason: $ADD_SCRIPT_TO_CHANGELOG"
fi