#!/bin/bash

#This script adds realm-admin role in realm-management client for uk-pmrv-app-api system user

SCRIPT_NAME=$(basename -- "$0")

set -e
KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

GET_UK_PMRV_API_SERVICE_USER_URL="$BASE_URL/admin/realms/master/clients/9d233e9a-ed07-4e83-b572-fd97aede8bdb/service-account-user"
SERVICE_USER=$(curl -s -L -G "$GET_UK_PMRV_API_SERVICE_USER_URL" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN")
SERVICE_USER_ID=$(echo "$SERVICE_USER" | jq -r '.id')

GET_MASTER_REALM_CLIENT_URL="$BASE_URL/admin/realms/master/clients"
MASTER_REALM_CLIENT=$(curl -s -L -G "$GET_MASTER_REALM_CLIENT_URL" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
  --data-urlencode "clientId=master-realm")
MASTER_REALM_CLIENT_ID=$(echo "$MASTER_REALM_CLIENT" | jq -r ".[0] | .id")

GET_QUERY_GROUPS_ROLE_URL="$BASE_URL/admin/realms/master/clients/$MASTER_REALM_CLIENT_ID/roles/query-groups"
QUERY_GROUPS_ROLE=$(curl -s -L -G "$GET_QUERY_GROUPS_ROLE_URL" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN")
QUERY_GROUPS_ROLE_ID=$(echo "$QUERY_GROUPS_ROLE" | jq -r '.id')

GET_QUERY_USERS_ROLE_URL="$BASE_URL/admin/realms/master/clients/$MASTER_REALM_CLIENT_ID/roles/query-users"
QUERY_USERS_ROLE=$(curl -s -L -G "$GET_QUERY_USERS_ROLE_URL" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN")
QUERY_USERS_ROLE_ID=$(echo "$QUERY_USERS_ROLE" | jq -r '.id')

GET_VIEW_USERS_ROLE_URL="$BASE_URL/admin/realms/master/clients/$MASTER_REALM_CLIENT_ID/roles/view-users"
VIEW_USERS_ROLE=$(curl -s -L -G "$GET_VIEW_USERS_ROLE_URL" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN")
VIEW_USERS_ROLE_ID=$(echo "$VIEW_USERS_ROLE" | jq -r '.id')

UPDATE_PMRV_API_SERVICE_USER_URL="$BASE_URL/admin/realms/master/users/$SERVICE_USER_ID/role-mappings/clients/$MASTER_REALM_CLIENT_ID"
PMRV_API_SERVICE_USER=$(curl -s -L -X POST "$UPDATE_PMRV_API_SERVICE_USER_URL" \
-H 'Content-Type: application/json' \
-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
--data-raw '[
  {
    "clientRole": true,
    "composite": true,
    "name": "query-groups",
    "id": "'$QUERY_GROUPS_ROLE_ID'"
  },
  {
    "clientRole": true,
    "composite": true,
    "name": "query-users",
    "id": "'$QUERY_USERS_ROLE_ID'"
  },
  {
    "clientRole": true,
    "composite": true,
    "name": "view-users",
    "id": "'$VIEW_USERS_ROLE_ID'"
  }
]')

if [ -z "$PMRV_API_SERVICE_USER" ]
then
	echo " $SCRIPT_NAME executed successfully"
else
	echo " $SCRIPT_NAME failed $PMRV_API_SERVICE_USER"
fi
