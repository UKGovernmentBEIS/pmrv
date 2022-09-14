export interface GovukSelectOption<T = any> {
  text: string;
  value: T;
  disabled?: boolean;
}
