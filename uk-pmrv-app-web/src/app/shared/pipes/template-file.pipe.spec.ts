import { TemplateFilePipe } from './template-file.pipe';

describe('TemplateFilePipe', () => {
  let pipe: TemplateFilePipe;

  beforeEach(() => (pipe = new TemplateFilePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map to respectable file', () => {
    expect(pipe.transform('UNREASONABLE_COST')).toEqual('Unreasonable Costs for UK ETS (MRR) v1.xlsx');
    expect(pipe.transform('MMP')).toEqual('p4_monitoring_methodology_plan_template_en.xlsx');
    expect(pipe.transform('RULES_SAMPLING')).toEqual('p4_monitoring_methodology_plan_template_en.xlsx');
  });
});
