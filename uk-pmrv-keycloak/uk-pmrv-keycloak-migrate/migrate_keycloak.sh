#!/bin/bash

#Script that must run in order to configure keycloak
#All scripts that have to be executed must be added in the changelog folder and declare here
#Each script should call addUserToChangeLogRealm function from '/common/functions' as last action, in order to keep changelogs and avoid conflicts and issues.

SCRIPT_NAME=$(basename -- "$0")

set -e

#Import other scripts
CURRENT_PATH="$(dirname "$0")"
source "$CURRENT_PATH/common/functions.sh"
source "$CURRENT_PATH/migrate_keycloak_vars.sh"

logMessage ">>>>>>>>>> Start keycloak configuration >>>>>>>>>>"

#Variables Declaration
CHANGELOG_PATH="changelog/"

#Scripts based on environment variables
UPDATELOG_PATH="updatelog/"

#Add here all scripts that need to be executed for configuring keycloak
#Scripts must be added with proper execution order
changelogScripts=(
"01_introduce_changelog_realm.sh" 
"02_create_uk_pmrv_realm.sh"
"03_configure_uk_pmrv_realm_otp_policy.sh"
"04_pmrv_browser_authentication_flow.sh"
"05_configure_pmrv_browser_conditional_otp.sh"
"06_bind_pmrv_browser_flow_to_realm_broswer.sh"
"07_configure_uk_pmrv_realm_smtp_host.sh"
"08_configure_events.sh"
"09_configure_uk_pmrv_realm_password_policy.sh"
"10_configure_session_and_token_timeouts.sh"
"11_configure_pkce_on_web_client.sh"
"12_configure_brute_force.sh"
"13_disable_revoker-refresh-token.sh"
"14_update_session_idle_timeout.sh"
"15_create_camunda_identity_service_client.sh"
"16_create_camunda_admin_group.sh"
"17_add_login_listener_event_handler.sh"
"18_update_uk_pmrv_realm_password_policy.sh"
)

updatelogScripts=(
#all scripts that update the realm must be executed prior to scripts that add roles
"01_update_client_uk_pmrv_web_app.sh"
"02_update_client_uk_pmrv_app_api.sh"
"03_update_client_camunda_identity_service.sh"
"04_update_smtp_host.sh"
#the below scripts must ALWAYS run after any script that updates the realm because every time the realm is updated, new service-account-user is created
"05_add_realm_admin_role_to_pmrv_api_service_user.sh"
"06_add_roles_to_camunda_identity_service_service_user.sh"
)

#Get already executed scripts as concatenated string
EXECUTED_SCRIPTS=$(getChangeLogRealmUsers)

#Loop through changelogScripts array and execute scripts that are not already executed
for script in "${changelogScripts[@]}"
do
	if [[ "$EXECUTED_SCRIPTS" == *"$script"* ]]; then
		logMessage "$script has already been applied"
	else
		logMessage "Running $script"
		var="${CHANGELOG_PATH}${script}"
		eval $CURRENT_PATH/$var
	fi
done

#Loop through updatelogScripts array and execute scripts
for script in "${updatelogScripts[@]}"
do
	logMessage "Running $script"
	var="${UPDATELOG_PATH}${script}"
	eval $CURRENT_PATH/$var
done

logMessage ">>>>>>>>>> Keycloak configuration finished >>>>>>>>>>"
