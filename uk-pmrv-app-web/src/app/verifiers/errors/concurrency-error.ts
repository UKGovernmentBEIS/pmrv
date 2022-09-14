import {
  buildSaveNotFoundError,
  buildSavePartiallyNotFoundError,
  buildViewNotFoundError,
  ConcurrencyError,
} from '../../error/concurrency-error/concurrency-error';

const verifierConcurrencyLink: Pick<ConcurrencyError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/verifiers'],
  fragment: 'verifier-users',
  linkText: 'Return to manage verifier users page',
};

export const savePartiallyNotFoundVerifierError = buildSavePartiallyNotFoundError().withLink(verifierConcurrencyLink);

export const saveNotFoundVerifierError = buildSaveNotFoundError().withLink(verifierConcurrencyLink);

export const viewNotFoundVerifierError = buildViewNotFoundError().withLink(verifierConcurrencyLink);

const siteContactsConcurrencyLink: Pick<ConcurrencyError, 'link' | 'linkText' | 'fragment'> = {
  link: ['/user/verifiers'],
  fragment: 'site-contacts',
  linkText: 'Return to site contacts page',
};

export const savePartiallyNotFoundSiteContactsError =
  buildSavePartiallyNotFoundError().withLink(siteContactsConcurrencyLink);

export const deleteUniqueActiveVerifierError = new ConcurrencyError(
  'You must have an active verifier admin on your account',
).withLink(verifierConcurrencyLink);
