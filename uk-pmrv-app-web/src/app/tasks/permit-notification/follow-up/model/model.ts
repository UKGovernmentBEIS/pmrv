export interface SummaryList {
  label: string;
  url?: string;
  order: number;
  prefix?: string;
  show?: boolean;
  styleClass?: string;
  styleClassLabel?: string;
  type?: 'number' | 'boolean' | 'date' | 'string' | 'files';
  isArray?: boolean;
  statusKey?: string;
}

export interface Files {
  downloadUrl: string;
  fileName: string;
}
