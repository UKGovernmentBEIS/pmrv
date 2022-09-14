# Manage your UK Emissions Trading Scheme (METS) reporting service 
This repo contains the source code for the Manage your UK Emissions Trading Scheme (METS) reporting service, which has a working/project title known as Permitting, Monitoring, Reporting and Verification (PMRV) service. These terms are used interchangebly in this document but refer to the same METS reporting service. 

The Permitting, Monitoring, Reporting and Verification (PMRV) service is a fundamental part of the UK Governments ambition to achieve Net Zero carbon emissions by 2050 and is regulated by the Monitoring and Reporting Regulation (MRR). Information about this regulation can be found at https://www.legislation.gov.uk/eur/2018/2066/contents. In summary, any organisation performing any activity covered by the UK ETS (https://www.gov.uk/government/publications/participating-in-the-uk-ets/participating-in-the-uk-ets) must hold a greenhouse gas emissions permit or hospital or small emitter permit (installations) or emissions monitoring plan (aircraft operators). Permits and emissions monitoring plans are issued by the UK ETS Regulators. Around 2000 major industry and aviation operators major industry and aviation operators across the UK are in scope of this service which means that they are only allowed to emit a certain level of CO2 per compliance cycle year and thus each needs a permit to do so. The PMRV service is managed and overseen by UK Government regulators and enables operators to apply for permits, define their plan to monitor emissions, verify emissions with independent bodies and provide emissions reports. Non-compliance can result in financial penalties.

The working application source code is managed and maintained by the private/public beta delivery supplier Trasys-Unisystems in their on-prem bitbucket repository. This repo is refreshed whenever there is a release deployed to the production environment. The service is written in Java and uses the Spring Boot 2 framework. Additionally, instead of making JDBC calls, the code utilises an Object relational Mapping Framework, JPA/Hibernate. Each api has the responsibility to create and maintain its schema using Liquibase.

This repository contains the following folders:

(1) uk-pmrv-app-admin - the main app course code repo

(2) uk-pmrv-app-api	- the main application api services

(3) uk-pmrv-app-maintenance-page - holding page for when there is an outage or planned downtime

(4) uk-pmrv-app-web - tbc

(5) uk-pmrv-clamav - virus checking of of any files uploaded to the service

(6) uk-pmrv-docs - tbc

(7) uk-pmrv-keycloak - Identity and Access Management (IAM) service	

(8) uk-pmrv-nginx	- tbc


Updated Sept 2022
