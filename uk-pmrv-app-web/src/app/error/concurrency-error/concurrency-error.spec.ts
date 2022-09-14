import { ConcurrencyError } from './concurrency-error';

describe('ConcurrencyError', () => {
  it('should create an instance', () => {
    expect(new ConcurrencyError('')).toBeTruthy();
  });
});
