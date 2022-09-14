import { ResponsibilityOption } from '../confirm-responsibility/responsibility';
import { OnshoreInstallation } from '../installation-type/installation';
import { SubmitApplicationActionPayload } from '../pipes/submit-application';
import {
  ApplicationSectionType,
  InstallationAccountApplicationState,
} from '../store/installation-account-application.state';

export const mockState: InstallationAccountApplicationState = {
  tasks: [
    {
      type: ApplicationSectionType.responsibility,
      title: 'Confirm responsibility for operating the installation',
      linkText: 'Installation operator responsibility',
      status: 'complete',
      link: 'responsibility',
      value: {
        responsibility: [
          ResponsibilityOption.control,
          ResponsibilityOption.effectivePermit,
          ResponsibilityOption.management,
        ],
      },
    },
    {
      type: ApplicationSectionType.legalEntity,
      title: 'Add the installation operator details',
      linkText: 'Identity and contact details of the operator',
      status: 'complete',
      link: 'legal-entity',
      value: {
        selectGroup: {
          isNew: true,
          id: null,
        },
        detailsGroup: {
          type: 'PARTNERSHIP',
          name: 'test',
          referenceNumber: 'ab123456',
          noReferenceNumberReason: null,
          address: {
            city: 'test',
            country: 'GR',
            line1: 'test',
            line2: 'test',
            postcode: 'test',
          },
        },
      },
    },
    {
      type: ApplicationSectionType.installation,
      title: 'Add the installation details',
      linkText: 'Installation details',
      status: 'complete',
      link: 'installation',
      value: {
        onshoreGroup: {
          name: 'test',
          siteName: 'test',
          address: {
            city: 'test',
            country: 'GR',
            line1: 'test',
            line2: 'test',
            postcode: 'test',
          },
          gridReference: 'te 1234 567 890',
        },
        locationGroup: { location: 'ENGLAND' },
        installationTypeGroup: { type: 'ONSHORE' },
      },
    },
    {
      type: ApplicationSectionType.etsScheme,
      title: 'Select the emissions trading scheme',
      linkText: 'ETS Scheme',
      status: 'not started',
      link: 'ets-scheme',
      value: {
        etsSchemeType: 'UK_ETS_INSTALLATIONS',
      },
    },
    {
      type: ApplicationSectionType.commencement,
      title: 'Add the commencement date of operations',
      linkText: 'Indicate the date of commencement of operations',
      status: 'complete',
      link: 'commencement',
      value: {
        commencementDate: new Date('2020-08-27'),
      },
    },
  ],
  taskId: 2,
};

export const mockOnshoreInstallation: OnshoreInstallation = {
  installationTypeGroup: { type: 'ONSHORE' },
  onshoreGroup: {
    name: 'Installation name',
    siteName: 'Site name',
    address: {
      line1: 'line',
      line2: null,
      city: 'town',
      country: 'GR',
      postcode: '1231',
    },
    gridReference: 'NN16721',
  },
  locationGroup: { location: 'ENGLAND' },
};

export const mockPayload: SubmitApplicationActionPayload = {
  payloadType: 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD',
  accountType: 'INSTALLATION',
  competentAuthority: 'ENGLAND',
  name: 'test',
  siteName: 'test',
  location: {
    type: 'ONSHORE',
    address: {
      city: 'test',
      country: 'GR',
      line1: 'test',
      line2: 'test',
      postcode: 'test',
    },
    gridReference: 'te 1234 567 890',
  },
  legalEntity: {
    type: 'PARTNERSHIP',
    name: 'test',
    referenceNumber: 'ab123456',
    address: {
      city: 'test',
      country: 'GR',
      line1: 'test',
      line2: 'test',
      postcode: 'test',
    },
  },
  emissionTradingScheme: 'UK_ETS_INSTALLATIONS',
  commencementDate: new Date('2020-08-27'),
};
