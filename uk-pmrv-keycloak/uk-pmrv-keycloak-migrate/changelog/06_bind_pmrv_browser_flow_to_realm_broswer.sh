#!/bin/bash

#This script selects the $PMRV_BROWSER flow as the browser flow of the $UK_PMRV_REALM_NAME realm.

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
UPDATE_REALM_URL="$BASE_URL/admin/realms/"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

#Bind flow to realm
BIND_FLOW_TO_REALM=$(curl -s -L -X PUT "$UPDATE_REALM_URL$UK_PMRV_REALM_NAME" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{
    "realm": "'"$UK_PMRV_REALM_NAME"'",
    "browserFlow": "'"$PMRV_BROWSER"'"
}')

if [ -z "$BIND_FLOW_TO_REALM" ]
then
	echo " Realm $UK_PMRV_REALM_NAME binding successfully"
else
	echo " Realm $UK_PMRV_REALM_NAME binding failed"
	exit;
fi

#Add script name as user to changelog realm for tracking purposes
ADD_SCRIPT_TO_CHANGELOG=$(addUserToChangeLogRealm "$SCRIPT_NAME")

if [ -z "$ADD_SCRIPT_TO_CHANGELOG" ]
then
	echo " Script $SCRIPT_NAME added to changelog"
else
	echo " Script $SCRIPT_NAME was not to added to changelog. Reason: $ADD_SCRIPT_TO_CHANGELOG"
fi
