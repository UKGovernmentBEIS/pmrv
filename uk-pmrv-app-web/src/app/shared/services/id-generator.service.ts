import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class IdGeneratorService {
  generateId(): string {
    return `${Date.now()}${Math.random()}`;
  }
}
