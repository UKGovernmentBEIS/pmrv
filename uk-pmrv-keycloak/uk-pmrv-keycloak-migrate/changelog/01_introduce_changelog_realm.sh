#!/bin/bash

#This script creates a realm with name changelog, that will serve as changelog tracker for keycloak configuration.
#To achieve this a user will be added to the changelog realm for each script that has been executed.

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
CREATE_REALM_URL="$BASE_URL/admin/realms"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)


#Create new realm
CREATE_REALM=$(curl -s -L -X POST "$CREATE_REALM_URL" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{
	"id": "'$CHANGELOG_REALM_NAME'",
	"realm": "'$CHANGELOG_REALM_NAME'",
	"enabled": false,
	"users": [
		{
			"username": "'$SCRIPT_NAME'",
			"enabled": false
		}
	]
}')

if [ -z "$CREATE_REALM" ]
then
	echo " Realm $CHANGELOG_REALM_NAME created successfully"
else
	echo " $CREATE_REALM"
fi
