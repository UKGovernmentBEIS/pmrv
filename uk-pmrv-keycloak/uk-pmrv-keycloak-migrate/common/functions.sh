#!/bin/bash

#Add Log Message
logMessage(){
	
	MSG=$1
	echo "$(date +'%Y-%m-%d %H:%M:%S,%3N') INFO  [migrate_keycloak] $MSG"
}
export -f logMessage

#Get Access Token From Keycloak Admin User 
getKeycloakAdminAccessToken(){
	#Variables
	CLIENT_ID=admin-cli
	GRANT_TYPE=password
	RETRIEVE_TOKEN_URL="$BASE_URL/realms/master/protocol/openid-connect/token"

	ACCESS_TOKEN=$(curl -s -L -X POST "$RETRIEVE_TOKEN_URL" \
	-H 'Content-Type: application/x-www-form-urlencoded' \
	--data-urlencode "client_id=$CLIENT_ID" \
	--data-urlencode "username=$KEYCLOAK_USER" \
	--data-urlencode "password=$KEYCLOAK_PASSWORD" \
	--data-urlencode "grant_type=$GRANT_TYPE" \
	| jq -r '.access_token')

	echo $ACCESS_TOKEN
}
export -f getKeycloakAdminAccessToken

#Add user to changelog realm
addUserToChangeLogRealm(){
	
	#Variables
	USERNAME=$1
	KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)
	CREATE_REALM_USER_URL="$BASE_URL/admin/realms/$CHANGELOG_REALM_NAME/users"
	
	CREATE_USER=$(curl -s -L -X POST "$CREATE_REALM_USER_URL" \
	-H 'Content-Type: application/json' \
	-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
	--data-raw '{
		"username": "'$USERNAME'",
		"enabled": false
	}')
	
	echo $CREATE_USER
}
export -f addUserToChangeLogRealm

#Retrieve all users of changelog realm as a concatenated string. Realm users represent executed scripts
getChangeLogRealmUsers(){

	#Variables
	KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)
	GET_REALM_USERS_URL="$BASE_URL/admin/realms/$CHANGELOG_REALM_NAME/users"

	USERS=$(curl -s -L -X GET "$GET_REALM_USERS_URL" \
	-H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN")
	
	echo $USERS
}
export -f getChangeLogRealmUsers
