import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-subtask-help',
  templateUrl: './subtask-help.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubtaskHelpComponent {
  readonly statusKey = this.route.snapshot.data.statusKey;
  constructor(private readonly route: ActivatedRoute) {}
}
