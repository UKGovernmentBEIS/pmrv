#!/bin/bash

#This script updates conditional OTP execution of PMRVBrowser authentication flow.

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
UPDATE_REALM_URL="$BASE_URL/admin/realms/"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

#Get PMRV Browser Conditional OTP execution id
GET_PMRV_BROWSER_EXECUTIONS=$(curl -s -L -X GET "$UPDATE_REALM_URL$UK_PMRV_REALM_NAME"/authentication/flows/"$PMRV_BROWSER"/executions \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN")

CONDITIONAL_OTP_ID=$(echo "$GET_PMRV_BROWSER_EXECUTIONS" | jq -r '.[] | select(.displayName=="PMRVBrowser Browser - Conditional OTP").id')

#Update Conditional OTP execution
CONDITIONAL_OTP_EXECUTION=$(curl -s -L -X PUT "$UPDATE_REALM_URL$UK_PMRV_REALM_NAME"/authentication/flows/"$PMRV_BROWSER"/executions \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{
    "id": "'"$CONDITIONAL_OTP_ID"'",
    "requirement": "REQUIRED"
}')

if [ -z "$CONDITIONAL_OTP_EXECUTION" ]
then
	#In case of error during realm creation, print the error and exit in order to avoid successfully loging the script execution
	echo " Realm $UK_PMRV_REALM_NAME updated failed"
	exit;
else
	echo " Realm $UK_PMRV_REALM_NAME updated successfully"
fi

#Add script name as user to changelog realm for tracking purposes
ADD_SCRIPT_TO_CHANGELOG=$(addUserToChangeLogRealm "$SCRIPT_NAME")

if [ -z "$ADD_SCRIPT_TO_CHANGELOG" ]
then
	echo " Script $SCRIPT_NAME added to changelog"
else
	echo " Script $SCRIPT_NAME was not to added to changelog. Reason: $ADD_SCRIPT_TO_CHANGELOG"
fi
