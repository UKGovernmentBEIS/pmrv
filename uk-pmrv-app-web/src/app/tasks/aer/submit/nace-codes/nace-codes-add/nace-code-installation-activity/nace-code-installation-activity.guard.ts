import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { AerService } from '@tasks/aer/core/aer.service';

@Injectable({ providedIn: 'root' })
export class NaceCodeInstallationActivityGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const subCategory = this.router.getCurrentNavigation()?.extras.state?.subCategory;
    return subCategory ? true : this.router.parseUrl(`tasks/${route.paramMap.get('taskId')}/aer/submit/nace-codes/add`);
  }
}
