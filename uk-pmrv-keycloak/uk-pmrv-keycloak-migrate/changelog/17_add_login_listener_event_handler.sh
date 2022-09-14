#!/bin/bash

#This adds the login listener event handler to keycloak Event handler configuration.

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
UPDATE_EVENTS_REALM_URL="$BASE_URL/admin/realms/$UK_PMRV_REALM_NAME/events/config"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

#Update smtp host
UPDATE_REALM=$(curl -s -L -X PUT "$UPDATE_EVENTS_REALM_URL" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{
	"eventsEnabled": true,
	"eventsListeners": [
		"email",
		"jboss-logging",
		"last-login-listener"
	],
	"adminEventsDetailsEnabled": true,
	"adminEventsEnabled": true
}')

if [ -z "$UPDATE_REALM" ]
then
	echo " Realm $UK_PMRV_REALM_NAME updated successfully"
else
	#In case of error during realm creation, print the error and exit in order to avoid successfully loging the script execution
	echo " $UPDATE_REALM"
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
