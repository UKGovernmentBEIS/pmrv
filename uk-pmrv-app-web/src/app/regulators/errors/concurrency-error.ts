import {
  buildSaveNotFoundError,
  buildSavePartiallyNotFoundError,
  buildViewNotFoundError,
  ConcurrencyError,
} from '../../error/concurrency-error/concurrency-error';

const regulatorConcurrencyLink: Pick<ConcurrencyError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/regulators'],
  linkText: 'Return to regulator users page',
  fragment: 'regulator-users',
};

export const saveNotFoundRegulatorError = buildSaveNotFoundError().withLink(regulatorConcurrencyLink);

export const savePartiallyNotFoundRegulatorError = buildSavePartiallyNotFoundError().withLink(regulatorConcurrencyLink);

export const viewNotFoundRegulatorError = buildViewNotFoundError().withLink(regulatorConcurrencyLink);

const siteContactConcurrencyLink: Pick<ConcurrencyError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/regulators'],
  linkText: 'Return to site contacts page',
  fragment: 'site-contacts',
};

export const savePartiallyNotFoundSiteContactError =
  buildSavePartiallyNotFoundError().withLink(siteContactConcurrencyLink);

const externalContactConcurrencyLink: Pick<ConcurrencyError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/regulators'],
  linkText: 'Return to external contacts page',
  fragment: 'external-contacts',
};

export const viewNotFoundExternalContactError = buildViewNotFoundError().withLink(externalContactConcurrencyLink);

export const saveNotFoundExternalContactError = buildSaveNotFoundError().withLink(externalContactConcurrencyLink);
