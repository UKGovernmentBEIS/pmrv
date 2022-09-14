import { SummaryList } from '../../../tasks/permit-notification/follow-up/model/model';

export const followUpSummaryListMapper: Record<
  keyof { followUpRequest: string; followUpResponseExpirationDate: string },
  SummaryList
> = {
  followUpRequest: { label: 'Request from the regulator', order: 1, type: 'string' },
  followUpResponseExpirationDate: { label: 'Due date', order: 2, type: 'date', url: 'due-date' },
};
