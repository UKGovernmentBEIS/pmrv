import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-summary-download-files',
  template: `
    <ng-container *ngFor="let file of files; let isLast = last">
      <a [routerLink]="file.downloadUrl" govukLink target="_blank">{{ file.fileName }}</a>
      <br *ngIf="!isLast && files.length !== 1" />
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDownloadFilesComponent {
  @Input()
  files: { downloadUrl: string; fileName: string }[];
}
