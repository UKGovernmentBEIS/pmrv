#!/bin/bash

#This script updates the uk-pmrv realm in order to configure brute force.

SCRIPT_NAME=$(basename -- "$0")

set -e

#Variables Declaration
UPDATE_REALM_URL="$BASE_URL/admin/realms/"

#Get Keyclok Admin Access Token using method from imported functions script
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

#Configures brute force on uk-pmrv realm
CONFIG_UK_PMRV_BRUTE_FORCE=$(curl -s -L -X PUT "$UPDATE_REALM_URL$UK_PMRV_REALM_NAME" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '{
    "bruteForceProtected": "true",
    "permanentLockout": "false",
    "failureFactor": "5",
    "quickLoginCheckMilliSeconds": "1000",
    "maxFailureWaitSeconds": "900",
    "minimumQuickLoginWaitSeconds": "60",
    "waitIncrementSeconds": "900",
    "maxDeltaTimeSeconds": "43200"
}')

if [ -z "$CONFIG_UK_PMRV_BRUTE_FORCE" ]
then
	echo "Realm $UK_PMRV_REALM_NAME updated successfully"
else
	#In case of error during realm creation, print the error and exit in order to avoid successfully logging the script execution
	echo "$CONFIG_UK_PMRV_BRUTE_FORCE"
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