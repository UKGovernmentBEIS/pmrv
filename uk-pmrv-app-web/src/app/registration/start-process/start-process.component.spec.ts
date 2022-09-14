import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../shared/shared.module';
import { StartProcessComponent } from './start-process.component';

describe('StartProcessComponent', () => {
  let component: StartProcessComponent;
  let fixture: ComponentFixture<StartProcessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [StartProcessComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StartProcessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render a continue button', () => {
    const element: HTMLElement = fixture.nativeElement;
    const anchors = element.querySelectorAll<HTMLAnchorElement>('a');
    expect(anchors[0].href).toContain('/email');
  });
});
