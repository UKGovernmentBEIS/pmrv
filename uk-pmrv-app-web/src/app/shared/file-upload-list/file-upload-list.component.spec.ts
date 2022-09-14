import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../testing';
import { FileUploadEvent } from '../file-input/file-upload-event';
import { FileUploadListComponent } from './file-upload-list.component';

describe('FileUploadListComponent', () => {
  let component: FileUploadListComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  @Component({
    template: `
      <app-file-upload-list
        [listTitle]="listTitle"
        [files]="files"
        (fileDelete)="onDelete($event)"
        [isDisabled]="isDisabled"
      ></app-file-upload-list>
    `,
  })
  class TestComponent {
    listTitle: string;
    files: FileUploadEvent[] = [];
    onDelete = jest.fn<any, [number]>();
    isDisabled = false;
  }

  class Page extends BasePage<TestComponent> {
    get listTitle() {
      return this.query<HTMLDivElement>('.govuk-heading-m');
    }

    get hidden() {
      return this.query<HTMLDivElement>('.moj-hidden');
    }

    get rows() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__row');
    }

    get files() {
      return this.queryAll<HTMLDataElement>('.moj-multi-file-upload__message');
    }

    get deleteButtons() {
      return this.queryAll<HTMLButtonElement>('.moj-multi-file-upload__delete');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [FileUploadListComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(FileUploadListComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list title', () => {
    const listTitle = 'This is a list title';
    hostComponent.listTitle = listTitle;
    fixture.detectChanges();

    expect(page.listTitle.textContent).toBe(listTitle);
  });

  it('should hide the element when there are no items', () => {
    expect(page.hidden).toBeTruthy();
  });

  it('should list the files and their status', () => {
    hostComponent.files = [
      { file: { name: 'Uploaded file' } as File, uuid: '1234', progress: null },
      { file: new File([], 'Test file'), uuid: '1254', errors: null, progress: 1 },
      { file: new File([], 'Test file 2'), uuid: null, errors: null, progress: 0.3 },
      { file: new File([], 'Test file 3'), uuid: null, errors: { upload: 'Could not upload' }, progress: null },
    ];
    fixture.detectChanges();

    expect(page.hidden).toBeFalsy();
    expect(page.rows).toHaveLength(4);
    expect(page.files.map((row) => row.textContent.trim())).toEqual([
      'Uploaded file',
      'Test file has been uploaded',
      'Test file 2 30%',
      'Could not upload',
    ]);
    expect(page.deleteButtons).toHaveLength(4);
  });

  it('should emit whenever a file is deleted', () => {
    hostComponent.onDelete.mockClear();
    hostComponent.files = [
      { file: { name: 'Uploaded file' } as File, uuid: '1234', progress: null },
      { file: new File([], 'Test file 3'), uuid: null, errors: { upload: 'Could not upload' }, progress: null },
    ];
    fixture.detectChanges();

    page.deleteButtons[0].click();
    fixture.detectChanges();

    expect(hostComponent.onDelete).toHaveBeenCalledWith(0);
  });

  it('should disable the delete button', () => {
    expect(page.deleteButtons.some((button) => button.disabled)).toBeFalsy();

    hostComponent.isDisabled = true;
    fixture.detectChanges();

    expect(page.deleteButtons.every((button) => button.disabled)).toBeTruthy();
  });
});
