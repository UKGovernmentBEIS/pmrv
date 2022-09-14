#!/bin/bash

#Contains variables commonly used and/or needed to customize keycloak environment

#Keycloak server url
export BASE_URL=http://localhost:$KEYCLOAK_HTTP_PORT/auth

#Name of realm used to hold the changelog of executed scripts
export CHANGELOG_REALM_NAME=changelog

#Name of realm used to hold configuration for PMRV application
export UK_PMRV_REALM_NAME=uk-pmrv

#PMRV authentication flow
export PMRV_BROWSER=PMRVBrowser
