import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { SourceStreamHelpTemplateComponent } from './source-stream-help-template.component';

describe('SourceStreamHelpTemplateComponent', () => {
  let component: SourceStreamHelpTemplateComponent;
  let fixture: ComponentFixture<SourceStreamHelpTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(SourceStreamHelpTemplateComponent);
    component = fixture.componentInstance;
  }

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
