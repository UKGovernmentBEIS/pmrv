import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-approaches-add-template',
  templateUrl: './approaches-add-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesAddTemplateComponent {
  @Input() form: FormGroup;
  @Input() monitoringApproaches: { [key: string]: any };
  @Output() readonly formSubmit = new EventEmitter<FormGroup>();

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }
}
