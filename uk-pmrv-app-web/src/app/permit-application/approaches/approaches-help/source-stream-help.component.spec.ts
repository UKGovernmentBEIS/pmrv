import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitApplicationModule } from '../../permit-application.module';
import { SourceStreamHelpComponent } from './source-stream-help.component';

describe('SourceStreamHelpComponent', () => {
  let component: SourceStreamHelpComponent;
  let fixture: ComponentFixture<SourceStreamHelpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
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
