import { ChangeDetectionStrategy, Component, ElementRef, Input, ViewChild } from '@angular/core';

@Component({
  selector: 'govuk-header',
  templateUrl: './header.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {
  @Input() logoSvg: string;
  @Input() title: string;
  @ViewChild('header') header: ElementRef;
}
