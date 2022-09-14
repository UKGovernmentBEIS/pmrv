export class ConcurrencyError {
  link: any[];
  linkText: string;
  fragment?: string;

  constructor(readonly heading: string) {}

  withLink?({ link, linkText, fragment }: Pick<ConcurrencyError, 'link' | 'linkText' | 'fragment'>): this {
    this.link = link;
    this.linkText = linkText;
    this.fragment = fragment;

    return this;
  }
}

export const buildViewNotFoundError = () =>
  new ConcurrencyError('This item cannot be viewed because the information no longer exists');

export const buildSaveNotFoundError = () =>
  new ConcurrencyError('These changes cannot be saved because the information no longer exists');

export const buildViewPartiallyNotFoundError = () =>
  new ConcurrencyError('This item cannot be viewed because some of the information no longer exists');

export const buildSavePartiallyNotFoundError = () =>
  new ConcurrencyError('These changes cannot be saved because some of the information no longer exists');
