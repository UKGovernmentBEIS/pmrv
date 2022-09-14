import { SecurityContext } from '@angular/core';

import { MarkdownModuleConfig, MarkedOptions } from 'ngx-markdown';

import { MarkdownRenderer } from './markdown-renderer';

export const markdownModuleConfig: MarkdownModuleConfig = {
  markedOptions: { provide: MarkedOptions, useValue: { renderer: new MarkdownRenderer() } },
  sanitize: SecurityContext.URL,
};
