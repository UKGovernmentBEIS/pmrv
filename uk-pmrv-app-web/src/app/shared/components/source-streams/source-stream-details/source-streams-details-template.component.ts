import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { descriptionOptions, typeOptions } from '@shared/components/source-streams/source-stream-options';

@Component({
  selector: 'app-source-streams-details-template',
  templateUrl: './source-streams-details-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SourceStreamDetailsTemplateComponent {
  @Input() form: FormGroup;
  @Input() isEditing: boolean;

  @Output() readonly formSubmit = new EventEmitter<FormGroup>();

  descriptionOptions = descriptionOptions;
  typeOptions = typeOptions;

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }
}
