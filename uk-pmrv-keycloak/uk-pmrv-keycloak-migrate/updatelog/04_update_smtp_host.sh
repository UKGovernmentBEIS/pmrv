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
		"starttls": "'$MAIL_TLS'",
		"auth": "'$MAIL_AUTH'",
		"port": "'$MAIL_PORT'",
		"host": "'$MAIL_HOST'",
		"from": "'$MAIL_FROM'",
		"ssl": "'$MAIL_SSL'",
		"user": "'$MAIL_USER'",
		"password": "'$MAIL_PASSWORD'"
	}
}')

if [ -z "$UPDATE_REALM" ]
then
	echo " $SCRIPT_NAME executed successfully"
else
	echo " $SCRIPT_NAME failed $UPDATE_REALM"
fi
