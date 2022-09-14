import { ChangeDetectorRef, Directive, ElementRef, OnInit, Optional, Renderer2 } from '@angular/core';

import { takeUntil } from 'rxjs';

import { PendingRequestService } from '../core/guards/pending-request.service';
import { DestroySubject } from '../core/services/destroy-subject.service';

@Directive({ selector: 'button[appPendingButton]', providers: [DestroySubject] })
export class PendingButtonDirective implements OnInit {
  constructor(
    @Optional() private readonly pendingRequest: PendingRequestService,
    private readonly changeDetectorRef: ChangeDetectorRef,
    private readonly renderer: Renderer2,
    private readonly elementRef: ElementRef,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    if (this.pendingRequest) {
      this.pendingRequest.isRequestPending$.pipe(takeUntil(this.destroy$)).subscribe((isDisabled) => {
        if (isDisabled) {
          this.renderer.setAttribute(this.elementRef.nativeElement, 'disabled', 'true');
        } else {
          this.renderer.removeAttribute(this.elementRef.nativeElement, 'disabled');
        }

        this.changeDetectorRef.markForCheck();
      });
    }
  }
}
