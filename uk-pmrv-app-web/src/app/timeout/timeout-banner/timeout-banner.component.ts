import { DOCUMENT } from '@angular/common';
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  Input,
  OnInit,
  Renderer2,
  ViewChild,
} from '@angular/core';

import { takeUntil } from 'rxjs';

import dialogPolyfill from 'dialog-polyfill';

import { DestroySubject } from '../../core/services/destroy-subject.service';
import { TimeoutBannerService } from './timeout-banner.service';

@Component({
  selector: 'app-timeout-banner',
  templateUrl: './timeout-banner.component.html',
  styleUrls: ['./timeout-banner.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class TimeoutBannerComponent implements OnInit, AfterViewInit {
  @Input() timeOffsetSeconds: number;
  @ViewChild('modal') readonly modal: ElementRef<HTMLDialogElement>;

  private overlayClass = 'govuk-timeout-warning-overlay';
  private lastFocusedElement = null;

  constructor(
    @Inject(DOCUMENT) private readonly document: Document,
    readonly timeoutBannerService: TimeoutBannerService,
    private readonly renderer: Renderer2,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.timeoutBannerService.isVisible$.pipe(takeUntil(this.destroy$)).subscribe((isVisible) => {
      isVisible ? this.showDialog() : this.hideDialog();
    });
  }

  ngAfterViewInit(): void {
    dialogPolyfill.registerDialog(this.modal.nativeElement);
  }

  isDialogOpen(): boolean {
    return this.modal && this.modal.nativeElement.getAttribute('open') === '';
  }

  showDialog(): void {
    if (!this.isDialogOpen()) {
      this.renderer.addClass(this.document.body, this.overlayClass);
      this.saveLastFocusedElement();
      this.modal.nativeElement.showModal();
      this.modal.nativeElement.setAttribute('tabindex', '-1');
      this.modal.nativeElement.focus();
    }
  }

  hideDialog(): void {
    if (this.isDialogOpen()) {
      this.renderer.removeClass(this.document.body, this.overlayClass);
      this.modal.nativeElement.removeAttribute('tabindex');
      this.modal.nativeElement.close();
      this.setFocusOnLastFocusedElement();
    }
  }

  saveLastFocusedElement(): void {
    this.lastFocusedElement =
      this.document.activeElement && this.document.activeElement !== this.document.body
        ? this.document.activeElement
        : this.document.querySelector(':focus');
  }

  setFocusOnLastFocusedElement(): void {
    if (this.lastFocusedElement) {
      this.lastFocusedElement.focus();
    }
  }

  continue(): void {
    this.timeoutBannerService.extendSession();
  }

  signOut(): void {
    this.timeoutBannerService.signOut();
  }
}
