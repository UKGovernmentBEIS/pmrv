import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-approaches-template',
  templateUrl: './approaches-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesTemplateComponent {
  constructor(private readonly router: Router, private readonly route: ActivatedRoute) {}

  goToHelpPage(page: string): void {
    window.open(
      this.router.serializeUrl(
        this.router.createUrlTree([page], {
          relativeTo: this.router.url.endsWith('summary') ? this.route.parent : this.route,
        }),
      ),
    );
  }
}
