import { DocumentTemplateDTO, NotificationTemplateDTO } from 'pmrv-api';

export const mockedEmailTemplate: NotificationTemplateDTO = {
  id: 1,
  name: 'Email Template',
  operatorType: 'INSTALLATION',
  subject: 'Email template subject',
  text: 'Email template text',
  eventTrigger: 'Event that triggers the email',
  workflow: 'PMRV workflow',
  lastUpdatedDate: new Date('2022-02-02').toISOString(),
};

export const mockedDocumentTemplate: DocumentTemplateDTO = {
  id: 1,
  name: 'Document Template',
  operatorType: 'INSTALLATION',
  workflow: 'PMRV workflow',
  lastUpdatedDate: new Date('2022-03-03').toISOString(),
  fileUuid: '5eb3cbd8-3a98-4350-9275-4540a74076f5',
  filename: 'DocumentTemplateFile.docx',
  notificationTemplates: [
    {
      id: 31,
      name: 'Email Template',
    },
  ],
};
