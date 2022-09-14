import { Slugger } from 'marked';
import { MarkedRenderer } from 'ngx-markdown';

export class MarkdownRenderer extends MarkedRenderer {
  heading(text: string, level: 1 | 2 | 3 | 4 | 5 | 6, raw: string, slugger: Slugger): string {
    switch (level) {
      case 1:
        return `<h1 class="govuk-heading-xl">${text}</h1>`;
      case 2:
        return `<h2 class="govuk-heading-l">${text}</h2>`;
      case 3:
        return `<h3 class="govuk-heading-m">${text}</h3>`;
      default:
        return super.heading(text, level, raw, slugger);
    }
  }

  link(href: string | null, title: string | null, text: string): string {
    return `<a href="${href}" routerLink="${href || ''}" govukLink>${text}</a>`;
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  list(body: string, ordered: boolean, start: number): string {
    return ordered
      ? `<ol class="govuk-list govuk-list--number">${body}</ol>`
      : `<ul class="govuk-list govuk-list--bullet">${body}</ul>`;
  }

  paragraph(text: string): string {
    return `<p class="govuk-body">${text}</p>`;
  }
}
