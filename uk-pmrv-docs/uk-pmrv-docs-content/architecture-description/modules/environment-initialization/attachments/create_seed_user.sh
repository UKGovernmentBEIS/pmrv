#!/bin/bash

set -e

export BASE_URL=${API_KEYCLOAK_SERVERURL:=http://localhost:8091/auth}
export UK_PMRV_REALM_NAME=${API_KEYCLOAK_REALM:=uk-pmrv}
export PGPASSWORD=password

getKeycloakAdminAccessToken(){
	RETRIEVE_TOKEN_URL="$BASE_URL/realms/$UK_PMRV_REALM_NAME/protocol/openid-connect/token"

	ACCESS_TOKEN=$(curl -s -L -X POST "$RETRIEVE_TOKEN_URL" \
	-H 'Content-Type: application/x-www-form-urlencoded' \
	--data-urlencode "client_id=uk-pmrv-app-api" \
	--data-urlencode "client_secret=bbdcd303-4567-45ef-aa31-dd9bcbeb2572" \
	--data-urlencode "grant_type=client_credentials" \
	| jq -r '.access_token')

	echo $ACCESS_TOKEN
}

getUserId(){
	USER_EMAIL=$1
	GET_UK_PMRV_ADMIN_USER_URL="$BASE_URL/admin/realms/$UK_PMRV_REALM_NAME/users"
  KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)

  USER=$(curl -s -L -G "$GET_UK_PMRV_ADMIN_USER_URL" \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
  --data-urlencode "email=$USER_EMAIL" \
  --data exact=true)

  if [ "$USER" != "[]" ]
    then
      echo $USER | jq -r ".[0] | .id"
    else
      echo ""
  fi
}

addUserToKeycloak(){
  USER_EMAIL=$1
  USER_PASSWORD=$2
  USER_FIRST_NAME=$3
  USER_LAST_NAME=$4

  KEYCLOAK_ADMIN_ACCESS_TOKEN=$(getKeycloakAdminAccessToken)
  USER_ID=$(getUserId $USER_EMAIL)
  if [ -n "$USER_ID" ]
    then
  	  echo "uk-pmrv user: $USER_EMAIL already exists"
  	  exit 1
    else
      CREATE_UK_PMRV_USER=$(curl -s -L -X POST "$BASE_URL/admin/realms/$UK_PMRV_REALM_NAME/users" \
      -H 'Content-Type: application/json' \
      -H "Authorization: Bearer $KEYCLOAK_ADMIN_ACCESS_TOKEN" \
      --data-raw '{
            "enabled": true,
            "emailVerified": true,
            "firstName": "'$USER_FIRST_NAME'",
            "lastName": "'$USER_LAST_NAME'",
            "email": "'$USER_EMAIL'",
            "attributes": {
              "status": "REGISTERED"
            },
            "credentials" : [{
              "temporary": true,
              "type": "password",
              "value": "'$USER_PASSWORD'"
              }]
      }')

      if [ -n "$CREATE_UK_PMRV_USER" ]
        then
          echo " $CREATE_UK_PMRV_USER"
          exit 1
      fi
  fi
}

executesql() {
  psql -U pmrv -h localhost -p 5433 -d pmrv << EOF
            DO \$\$
            DECLARE
                -- replace value with user id from keycloak server of the corresponding env
                P_REGULATOR_USER_ID VARCHAR :='$USER_ID';

                -- replace value with the desired CA
                -- available values are:
                --  ENGLAND, WALES, SCOTLAND, NORTHERN_IRELAND, OPRED
                P_COMPETENT_AUTHORITY VARCHAR := '$COMPETENT_AUTHORITY';

                -- replace with the permissions template of the regulator
                -- "regulator_admin_team","regulator_technical_officer","regulator_team_leader","ca_super_user","pmrv_super_user"
                P_REGULATOR_USER_ROLE_CODE VARCHAR DEFAULT 'ca_super_user';

                P_REGULATOR_USER_ROLE_ID BIGINT := 0;
                P_AU_AUTHORITY_SEQ_NEXTVAL BIGINT := 0;

            BEGIN
                -- retrieve id of the role code
                SELECT ID
                  INTO P_REGULATOR_USER_ROLE_ID
                  FROM AU_ROLE
                 WHERE CODE = P_REGULATOR_USER_ROLE_CODE;

                IF P_REGULATOR_USER_ROLE_ID IS NULL THEN
                    RAISE exception 'Role with code regulator_user does not exist';
                END IF;

                -- validate CA
                IF NOT EXISTS (
                        SELECT ca
                          FROM (VALUES ('ENGLAND'),('WALES'),('SCOTLAND'),('NORTHERN_IRELAND'),('OPRED')) AS t (ca)
                         WHERE ca = P_COMPETENT_AUTHORITY)  THEN
                    RAISE exception 'Competent authority does not exist';
                END IF;

                SELECT NEXTVAL('au_authority_seq')
                INTO P_AU_AUTHORITY_SEQ_NEXTVAL;

                -- insert into authority table record for regulator_user
                INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, created_by)
                VALUES (P_AU_AUTHORITY_SEQ_NEXTVAL, P_REGULATOR_USER_ID, P_REGULATOR_USER_ROLE_CODE, 'ACTIVE', null, P_COMPETENT_AUTHORITY, P_REGULATOR_USER_ID);

                -- insert into authority_permission table all role_permissions for role 'regulator_user'
                INSERT INTO au_authority_permission (id, authority_id, permission)
                SELECT nextval('au_authority_permission_seq'), P_AU_AUTHORITY_SEQ_NEXTVAL, PERMISSION
                  FROM AU_ROLE_PERMISSION
                 WHERE ROLE_ID = P_REGULATOR_USER_ROLE_ID;
            END
            \$\$
EOF
}

addAuthoritiesToUser() {
USER_ID=$(getUserId $1)

if [ -z "$USER_ID" ]
    then
  	  echo "uk-pmrv user: $USER_EMAIL does not exist"
  	  exit 1
    else
      executesql
  fi
}

if [ "$#" != 5 ]
 then
   echo "5 arguments: CA, user email, user password, user first name, user last name required, $# provided"
   exit;
 else
   declare -A CAs=([ENGLAND]=1  [WALES]=1  [SCOTLAND]=1 [NORTHERN_IRELAND]=1 [OPRED]=1)
   if [[ -z "${CAs[$1]}" ]]
    then
      echo CA must be one of ENGLAND, WALES, SCOTLAND, NORTHERN_IRELAND, OPRED
      exit;
   fi
fi
COMPETENT_AUTHORITY=$1
USER_EMAIL=$2
USER_PASSWORD=$3
USER_FIRST_NAME=$4
USER_LAST_NAME=$5
addUserToKeycloak $USER_EMAIL $USER_PASSWORD $USER_FIRST_NAME $USER_LAST_NAME
addAuthoritiesToUser $USER_EMAIL $COMPETENT_AUTHORITY