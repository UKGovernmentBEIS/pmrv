import { buildSaveNotFoundError, ConcurrencyError } from '../../error/concurrency-error/concurrency-error';

export const taskNotFoundError = buildSaveNotFoundError().withLink({
  link: ['/dashboard'],
  linkText: 'Return to dashboard',
});

export const requestTaskReassignedError = () =>
  new ConcurrencyError('These changes cannot be saved because the task has been reassigned').withLink({
    link: ['/dashboard'],
    linkText: 'Return to dashboard',
  });
