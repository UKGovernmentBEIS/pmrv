#!/bin/bash

#This script creates camunda-admin group in master realm

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
UPDATE_REALM_URL="$BASE_URL/admin/realms"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

CONFIG_CAMUNDA_ADMIN_GROUP=$(curl -s -L -X POST "$UPDATE_REALM_URL/master/groups" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{"name":"camunda-admin"}')

if [ -z "$CONFIG_CAMUNDA_ADMIN_GROUP" ]
then
	echo "Realm master updated successfully"
else
	echo "$CONFIG_CAMUNDA_ADMIN_GROUP"
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