import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'templateFile' })
export class TemplateFilePipe implements PipeTransform {
  transform(type: 'UNREASONABLE_COST' | 'MMP' | 'RULES_SAMPLING'): string {
    switch (type) {
      case 'UNREASONABLE_COST':
        return 'Unreasonable Costs for UK ETS (MRR) v1.xlsx';
      case 'MMP':
      case 'RULES_SAMPLING':
        return 'p4_monitoring_methodology_plan_template_en.xlsx';
    }
  }
}
