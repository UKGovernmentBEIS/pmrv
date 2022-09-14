# UK PMRV Keycloak Config scripts

Contains scripts that should be executed in order to configure keycloak.

# Structure

## migrate_keycloak.sh script
Master script that should be executed in order to configure keycloak.
Scripts included in the changelog folder are declared here and executed conditionally.
Script internally checks for already executed scripts and run only those that have not previously run.

## migrate_keycloak_vars.sh script
Common variables that are used globally and variables that are modified among different environments (e.g. server urls) should be declared here
iin order to facilitate - among others - propagatation of keycloak configuration in different environments.

## changelog folder
Contains scripts that will be executed from the migrate_keycloak.sh master script.


##common
Contains scripts and functions used commonly.

#How to maintain scripts
In case you need to add/modify/delete keycloak configuration, a new script should be added in the changelog folder.
New changelog script should be added to the changelogScripts array declared in the 'migrate_keycloak.sh' script , in order to be executed when upgrade keycloak configuration.
Moreover in the end of the new changelog script call the addUserToChangeLogRealmaddUserToChangeLogRealm common function (see common folder) in order to track changelog execution.




