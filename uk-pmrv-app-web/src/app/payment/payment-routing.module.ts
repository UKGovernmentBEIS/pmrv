import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { CancelledComponent } from './cancelled/cancelled.component';
import { CompletedComponent } from './completed/completed.component';
import { PaymentRoute } from './core/payment-route.interface';
import { BankTransferComponent } from './make/bank-transfer/bank-transfer.component';
import { BankTransferGuard } from './make/bank-transfer/bank-transfer.guard';
import { ConfirmationComponent } from './make/confirmation/confirmation.component';
import { ConfirmationGuard } from './make/confirmation/confirmation.guard';
import { DetailsComponent } from './make/details/details.component';
import { MarkPaidComponent } from './make/mark-paid/mark-paid.component';
import { MarkPaidGuard } from './make/mark-paid/mark-paid.guard';
import { NotSuccessComponent } from './make/not-success/not-success.component';
import { OptionsComponent } from './make/options/options.component';
import { PaidComponent } from './paid/paid.component';
import { PaymentGuard } from './payment.guard';
import { PaymentActionGuard } from './payment-action.guard';
import { PaymentExistGuard } from './payment-exist.guard';
import { ReceivedComponent } from './received/received.component';
import { CancelComponent } from './track/cancel/cancel.component';
import { MarkPaidComponent as TrackMarkPaidComponent } from './track/mark-paid/mark-paid.component';
import { TrackComponent } from './track/track.component';

const taskRoutes: PaymentRoute[] = [
  {
    path: 'make',
    children: [
      {
        path: 'details',
        component: DetailsComponent,
        data: { pageTitle: 'Make payment details' },
        canActivate: [PaymentExistGuard],
      },
      {
        path: 'options',
        component: OptionsComponent,
        data: { pageTitle: 'Make payment options' },
        canActivate: [PaymentExistGuard],
      },
      {
        path: 'bank-transfer',
        component: BankTransferComponent,
        data: { pageTitle: 'Make payment by bank transfer' },
        canActivate: [BankTransferGuard],
      },
      {
        path: 'mark-paid',
        component: MarkPaidComponent,
        data: { pageTitle: 'Mark payment as paid' },
        canActivate: [MarkPaidGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        component: ConfirmationComponent,
        data: { pageTitle: 'Payment Confirmation' },
        canActivate: [ConfirmationGuard],
      },
      {
        path: 'not-success',
        component: NotSuccessComponent,
        data: { pageTitle: 'Payment not completed' },
      },
    ],
  },
  {
    path: 'track',
    children: [
      {
        path: '',
        component: TrackComponent,
        data: { pageTitle: 'Payment tracking details' },
      },
      {
        path: 'mark-paid',
        component: TrackMarkPaidComponent,
        data: { pageTitle: 'Mark payment as received' },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'cancel',
        component: CancelComponent,
        data: { pageTitle: 'Cancel payment' },
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
];

const actionRoutes: PaymentRoute[] = [
  {
    path: 'paid',
    component: PaidComponent,
    data: { pageTitle: 'Payment marked as paid' },
  },
  {
    path: 'cancelled',
    component: CancelledComponent,
    data: { pageTitle: 'Payment task cancelled' },
  },
  {
    path: 'received',
    component: ReceivedComponent,
    data: { pageTitle: 'Payment task received' },
  },
  {
    path: 'completed',
    component: CompletedComponent,
    data: { pageTitle: 'Payment completed' },
  },
];

const routes: PaymentRoute[] = [
  {
    path: ':taskId',
    canActivate: [PaymentGuard],
    canDeactivate: [PaymentGuard],
    children: taskRoutes,
  },
  {
    path: 'action/:actionId',
    canActivate: [PaymentActionGuard],
    canDeactivate: [PaymentActionGuard],
    children: actionRoutes,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PaymentRoutingModule {}
