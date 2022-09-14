import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ConcurrencyErrorComponent } from './concurrency-error/concurrency-error.component';
import { InternalServerErrorComponent } from './internal-server-error/internal-server-error.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

const routes: Routes = [
  {
    path: '500',
    data: { pageTitle: 'Sorry, there is a problem with the service' },
    component: InternalServerErrorComponent,
  },
  {
    path: 'concurrency',
    component: ConcurrencyErrorComponent,
  },
  {
    path: '404',
    data: { pageTitle: 'Page not found' },
    component: PageNotFoundComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ErrorRoutingModule {}
