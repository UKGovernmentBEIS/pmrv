import { AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'govuk-notification-banner',
  templateUrl: './notification-banner.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationBannerComponent implements AfterViewInit, OnInit {
  @Input() type: 'success' | 'neutral' = 'neutral';
  @Input() heading?: string;

  tabIndex: number | null;

  @ViewChild('banner') private readonly bannerElement: ElementRef<HTMLDivElement>;

  ngOnInit(): void {
    this.heading = this.heading ?? (this.type === 'success' ? 'Success' : 'Important');
    this.tabIndex = this.type === 'success' ? -1 : null;
  }

  ngAfterViewInit(): void {
    if (this.tabIndex) {
      this.bannerElement.nativeElement.focus();
    }
  }
}
