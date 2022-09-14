import {
  buildSaveNotFoundError,
  buildSavePartiallyNotFoundError,
  buildViewNotFoundError,
  ConcurrencyError,
} from '../../error/concurrency-error/concurrency-error';

const verificationBodyConcurrencyLink: Pick<ConcurrencyError, 'link' | 'linkText'> = {
  linkText: 'Return to manage verification bodies',
  link: ['/verification-bodies'],
};

export const savePartiallyNotFoundVerificationBodyError = buildSavePartiallyNotFoundError().withLink(
  verificationBodyConcurrencyLink,
);

export const saveNotFoundVerificationBodyError = buildSaveNotFoundError().withLink(verificationBodyConcurrencyLink);

export const viewNotFoundVerificationBodyError = buildViewNotFoundError().withLink(verificationBodyConcurrencyLink);

export const disabledVerificationBodyError = new ConcurrencyError(
  'This action cannot be performed because the verification body has been disabled',
).withLink(verificationBodyConcurrencyLink);
