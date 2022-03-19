import { TestBed } from '@angular/core/testing';

import { DestapiService } from './destapi.service';

describe('DestapiService', () => {
  let service: DestapiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DestapiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
