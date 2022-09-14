import { ViewportScroller } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { Router, Scroll } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ScrollService } from './scroll.service';

describe('FormService', () => {
  let service: ScrollService;
  let router: Router;
  let viewportScroller: ViewportScroller;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    });
    service = TestBed.inject(ScrollService);
    router = TestBed.inject(Router);
    viewportScroller = TestBed.inject(ViewportScroller);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should skip fragment scroll if provided with the scrollSkip flag', () => {
    const scrollPositionSpy = jest.spyOn(viewportScroller, 'scrollToPosition').mockImplementation();
    const scrollAnchorSpy = jest.spyOn(service as any, 'focusTargetFragment').mockImplementation();
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { scrollSkip: true } } } as any);
    (router.events as any).next(new Scroll(null, null, 'test'));

    expect(scrollPositionSpy).not.toHaveBeenCalled();
    expect(scrollAnchorSpy).not.toHaveBeenCalled();
  });

  it('should scroll to fragment if not provided with the scrollSkip flag', () => {
    const scrollPositionSpy = jest.spyOn(viewportScroller, 'scrollToPosition').mockImplementation();
    const scrollAnchorSpy = jest.spyOn(service as any, 'focusTargetFragment').mockImplementation();
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: {} } } as any);
    (router.events as any).next(new Scroll(null, null, 'test'));

    expect(scrollAnchorSpy).toHaveBeenCalled();
    expect(scrollPositionSpy).not.toHaveBeenCalled();
  });
});
