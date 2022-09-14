import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { AerService } from '@tasks/aer/core/aer.service';

@Injectable({ providedIn: 'root' })
export class NaceCodeSubCategoryGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const mainActivity = this.router.getCurrentNavigation()?.extras.state?.mainActivity;
    return mainActivity
      ? true
      : this.router.parseUrl(`tasks/${route.paramMap.get('taskId')}/aer/submit/nace-codes/add`);
  }
}
