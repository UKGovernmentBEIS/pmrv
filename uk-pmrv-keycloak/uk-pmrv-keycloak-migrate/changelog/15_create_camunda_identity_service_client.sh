#!/bin/bash

#This script creates pmrv-admin client in master realm

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
UPDATE_REALM_URL="$BASE_URL/admin/realms"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

CONFIG_CAMUNDA_ADMIN_CLIENT=$(curl -s -L -X POST "$UPDATE_REALM_URL/master/clients" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{
  "id": "9d233e9a-ed07-4e83-b572-fd97aede8bdb",
  "clientId":"camunda-identity-service",
  "enabled":true,
  "clientAuthenticatorType": "client-secret",
  "serviceAccountsEnabled": true,
  "bearerOnly": false,
  "attributes":{},
  "publicClient" : false,
  "secret" : "'$CAMUNDA_IDENTITY_SERVICE_SECRET'",
	"redirectUris" : [ "'$PMRV_ADMIN_APP_URL'/admin/*" ],
	"webOrigins": ["'$PMRV_ADMIN_APP_URL'/admin"],
  "protocol":"openid-connect"}')

if [ -z "$CONFIG_CAMUNDA_ADMIN_CLIENT" ]
then
	echo "Realm master updated successfully"
else
	echo "$CONFIG_CAMUNDA_ADMIN_CLIENT"
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