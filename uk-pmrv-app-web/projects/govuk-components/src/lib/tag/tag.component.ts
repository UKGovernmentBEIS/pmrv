import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { TagColor } from './tag-color.type';

@Component({
  selector: 'govuk-tag',
  templateUrl: './tag.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TagComponent {
  @Input() color: TagColor;
}
