#!/bin/bash

#This script updates realm with smtp host configuration.

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
UPDATE_REALM_URL="$BASE_URL/admin/realms/$UK_PMRV_REALM_NAME"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

#Update smtp host
UPDATE_REALM=$(curl -s -L -X PUT "$UPDATE_REALM_URL" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{
	"smtpServer": {
		"replyToDisplayName": "",
		"starttls": "'$MAIL_TLS'",
		"auth": "'$MAIL_AUTH'",
		"port": "'$MAIL_PORT'",
		"host": "'$MAIL_HOST'",
		"replyTo": "",
		"from": "'$MAIL_FROM'",
		"fromDisplayName": "",
		"envelopeFrom": "",
		"ssl": "'$MAIL_SSL'",
		"user": "'$MAIL_USER'",
		"password": "'$MAIL_PASSWORD'"
	},
	"emailTheme": "uk-pmrv-theme"
}')

if [ -z "$UPDATE_REALM" ]
then
	echo " Realm $UK_PMRV_REALM_NAME created successfully"
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
