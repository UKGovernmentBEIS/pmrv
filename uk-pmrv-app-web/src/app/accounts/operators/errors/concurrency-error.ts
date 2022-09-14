import {
  buildSaveNotFoundError,
  buildSavePartiallyNotFoundError,
  buildViewNotFoundError,
  buildViewPartiallyNotFoundError,
  ConcurrencyError,
} from '../../../error/concurrency-error/concurrency-error';

const buildOperatorConcurrencyLink: (accountId: number) => Pick<ConcurrencyError, 'link' | 'linkText' | 'fragment'> = (
  accountId,
) => ({
  link: ['/accounts', accountId],
  linkText: 'Return to the users, contacts and verifiers page',
  fragment: 'users',
});

const operatorErrorWithAccountIdFactory = (errorFactory: () => ConcurrencyError) => (accountId: number) =>
  errorFactory().withLink(buildOperatorConcurrencyLink(accountId));

export const viewPartiallyNotFoundOperatorError = operatorErrorWithAccountIdFactory(buildViewPartiallyNotFoundError);

export const savePartiallyNotFoundOperatorError = operatorErrorWithAccountIdFactory(buildSavePartiallyNotFoundError);

export const saveNotFoundOperatorError = operatorErrorWithAccountIdFactory(buildSaveNotFoundError);

export const viewNotFoundOperatorError = operatorErrorWithAccountIdFactory(buildViewNotFoundError);

export const saveNotFoundVerificationBodyError = (accountId: number) =>
  buildSaveNotFoundError().withLink({
    link: ['/accounts', accountId, 'verification-body', 'appoint'],
    linkText: 'Return to appoint a verifier page',
  });

export const appointedVerificationBodyError = (accountId: number) =>
  new ConcurrencyError('A verification body is already appointed.').withLink({
    link: ['/accounts', accountId],
    linkText: 'Return to users, contacts and verifiers page',
    fragment: 'users',
  });

export const activeOperatorAdminError = operatorErrorWithAccountIdFactory(
  () => new ConcurrencyError('You must have an active operator admin on your account'),
);

export const primaryContactError = operatorErrorWithAccountIdFactory(
  () => new ConcurrencyError('You must have a primary contact on your account'),
);

export const financialContactError = operatorErrorWithAccountIdFactory(
  () => new ConcurrencyError('You must have a financial contact on your account'),
);

export const serviceContactError = operatorErrorWithAccountIdFactory(
  () => new ConcurrencyError('You must have a service contact on your account'),
);
