import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { AddComponent } from './add/add.component';
import { AddComponent as ContactsAddComponent } from './contacts/add/add.component';
import { ContactsComponent } from './contacts/contacts.component';
import { DeleteComponent } from './delete/delete.component';
import { DetailsComponent } from './details/details.component';
import { FormComponent } from './form/form.component';
import { VerificationBodyTypePipe } from './form/verification-body-type.pipe';
import { VerificationBodiesComponent } from './verification-bodies.component';
import { VerificationBodiesRoutingModule } from './verification-bodies-routing.module';

@NgModule({
  declarations: [
    AddComponent,
    ContactsAddComponent,
    ContactsComponent,
    DeleteComponent,
    DetailsComponent,
    FormComponent,
    VerificationBodiesComponent,
    VerificationBodyTypePipe,
  ],
  imports: [SharedModule, SharedUserModule, VerificationBodiesRoutingModule],
})
export class VerificationBodiesModule {}
