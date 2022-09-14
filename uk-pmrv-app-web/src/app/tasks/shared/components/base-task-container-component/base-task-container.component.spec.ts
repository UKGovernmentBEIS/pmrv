import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';

import { TaskLayoutComponent } from '../task-layout/task-layout.component';
import { BaseTaskContainerComponent } from './base-task-container.component';

describe('BaseTaskContainerComponent', () => {
  let component: BaseTaskContainerComponent;
  let fixture: ComponentFixture<BaseTaskContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService],
      declarations: [BaseTaskContainerComponent, TaskLayoutComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BaseTaskContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
