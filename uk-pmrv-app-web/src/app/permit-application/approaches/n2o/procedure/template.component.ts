import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-template',
  templateUrl: './template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TemplateComponent {
  @Input() taskKey: string;
}
