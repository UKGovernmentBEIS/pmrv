import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AerModule } from '@tasks/aer/aer.module';
import { KeycloakService } from 'keycloak-angular';

import { SourceStreamHelpComponent } from './source-stream-help.component';

describe('SourceStreamHelpComponent', () => {
  let component: SourceStreamHelpComponent;
  let fixture: ComponentFixture<SourceStreamHelpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(SourceStreamHelpComponent);
    component = fixture.componentInstance;
  }

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
