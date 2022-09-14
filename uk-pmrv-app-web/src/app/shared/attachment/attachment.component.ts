import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-attachment',
  templateUrl: './attachment.component.html',
  styleUrls: ['./attachment.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AttachmentComponent {
  @Input() title: string;
  @Input() url: string;
  @Input() type: string;
  @Input() size: string;
}
