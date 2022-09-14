import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerModule } from '@tasks/aer/aer.module';
import { KeycloakService } from 'keycloak-angular';

import { ApproachesHelpComponent } from './approaches-help.component';

describe('ApproachesHelpComponent', () => {
  let component: ApproachesHelpComponent;
  let fixture: ComponentFixture<ApproachesHelpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(ApproachesHelpComponent);
    component = fixture.componentInstance;
  }

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
