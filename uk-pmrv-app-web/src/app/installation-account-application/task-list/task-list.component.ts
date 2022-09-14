import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { TaskSection } from '@shared/task-list/task-list.interface';

import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Component({
  selector: 'app-installation-task-list',
  template: `
    <app-task-list
      (submitApplication)="submit()"
      (cancelApplication)="cancel()"
      [isSubmitDisabled]="isSubmitDisabled$ | async"
      [sections]="sections$ | async"
      heading="Request to open an installation account"
      submitLabel="Check and submit your request"
      submitButtonLabel="Continue"
      cancelLinkLabel="Cancel this request"
    ></app-task-list>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskListComponent implements OnInit {
  isSubmitDisabled$: Observable<boolean>;
  sections$: Observable<TaskSection<ApplicationSectionType>[]> = this.store.select('tasks').pipe(
    map((tasks) =>
      tasks.map((task) => ({
        type: task.type,
        title: task.title,
        tasks: [
          {
            type: task.type,
            linkText: task.linkText,
            link: task.link,
            status: task.status,
          },
        ],
      })),
    ),
  );

  constructor(
    public readonly store: InstallationAccountApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.isSubmitDisabled$ = this.store.pipe(map(({ tasks }) => tasks.some((task) => task.status !== 'complete')));
  }

  submit(): void {
    this.store.setState({ ...this.store.getState(), isSummarized: true });
    this.router.navigate(['summary'], { relativeTo: this.route });
  }

  cancel(): void {
    this.router.navigate(['cancel'], { relativeTo: this.route });
  }
}
