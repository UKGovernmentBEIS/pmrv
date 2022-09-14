#!/bin/bash

#This script creates a new realm named uk-pmrv and adds basic configuration including :
#	a)A realm role named pmrv_user defined as default realm role
#	b)A user defined as realm admin
#	c)Two clients, uk-pmrv-app-api(confidential) and uk-pmrv-web-app(public)

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
	"id": "'$UK_PMRV_REALM_NAME'",
	"realm": "'$UK_PMRV_REALM_NAME'",
	"enabled": true,
	"registrationAllowed" : true,
	"registrationEmailAsUsername" : true,
	"roles": {
		"realm": [
		  {
			"name": "pmrv_user",
			"description": "PMRV User"
		  }
		]
	},
	"defaultRoles" : [ "offline_access", "pmrv_user", "uma_authorization" ],
	"loginTheme": "uk-pmrv-theme",
	"clients": [
		{
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
			"directAccessGrantsEnabled": true
		},
		{
			"id": "a2d1abe6-f362-422a-a17a-e0a2d566a265",
			"clientId": "uk-pmrv-app-api",
			"enabled": true,
			"protocol": "openid-connect",
			"attributes": {},
			"secret" : "'$PMRV_APP_API_CLIENT_SECRET'",
			"redirectUris" : [ "'$PMRV_API_APP_URL'" ],
			"webOrigins": ["'$PMRV_API_APP_URL'", "'$PMRV_WEB_APP_URL'"],
			"serviceAccountsEnabled" : true,
			"authorizationServicesEnabled" : true,
			"publicClient" : false
		}
	]
}')

if [ -z "$CREATE_REALM" ]
then
	echo " Realm $UK_PMRV_REALM_NAME created successfully"
else
	#In case of error during realm creation, print the error and exit in order to avoid successfully loging the script execution
	echo " $CREATE_REALM"
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
