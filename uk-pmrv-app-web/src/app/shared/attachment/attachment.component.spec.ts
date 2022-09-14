import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { BasePage } from '../../../testing';
import { AttachmentComponent } from './attachment.component';

describe('AttachmentComponent', () => {
  @Component({
    template: '<app-attachment [title]="title" [url]="url" [type]="type" [size]="size"></app-attachment>',
  })
  class TestComponent {
    title: string;
    url: string;
    type: string;
    size: string;
  }
  let component: AttachmentComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  class Page extends BasePage<TestComponent> {
    get title(): string {
      return this.query<HTMLAnchorElement>('.gem-c-attachment__link').textContent.trim();
    }

    get url(): string {
      return this.query<HTMLAnchorElement>('.gem-c-attachment__link').href;
    }

    get type(): string {
      return this.query<HTMLElement>('.gem-c-attachment__abbr').textContent.trim();
    }

    get size(): string {
      return this.query<HTMLSpanElement>('.gem-c-attachment__size').textContent;
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AttachmentComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(AttachmentComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the properties set', () => {
    hostComponent.title = 'Attachment title';
    hostComponent.url = 'files/attachment.xlsx';
    hostComponent.type = 'CSV';
    hostComponent.size = '300KB';
    fixture.detectChanges();

    expect(page.title).toBe('Attachment title');
    expect(page.url).toContain('files/attachment.xlsx');
    expect(page.type).toBe('CSV');
    expect(page.size).toBe('300KB');
  });
});
