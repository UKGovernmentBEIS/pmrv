FROM jboss/keycloak:16.1.0

USER root
RUN microdnf clean all
RUN microdnf update -y
RUN microdnf install -y curl
RUN microdnf install -y dos2unix findutils
RUN microdnf clean all
RUN curl -L https://github.com/stedolan/jq/releases/download/jq-1.6/jq-linux64 -o /bin/jq
RUN chmod 755 /bin/jq

COPY startup_and_migrate.sh /opt/jboss/tools/
COPY uk-pmrv-keycloak-migrate/ /opt/jboss/tools/migrations/
COPY startup-scripts/ /opt/jboss/startup-scripts/

RUN chown -R jboss:jboss /opt/jboss/tools/
RUN chmod +x -R /opt/jboss/tools/
RUN dos2unix /opt/jboss/tools/startup_and_migrate.sh
RUN find /opt/jboss/tools/migrations -type f -print0 | xargs -0 dos2unix

USER 1000
COPY uk-pmrv-keycloak-theme/target/uk-pmrv-keycloak-theme.jar /opt/jboss/keycloak/standalone/deployments/
COPY uk-pmrv-keycloak-api/target/uk-pmrv-keycloak-api.jar /opt/jboss/keycloak/standalone/deployments/

ENTRYPOINT []
CMD ["sh", "-c", "/opt/jboss/tools/startup_and_migrate.sh"]
