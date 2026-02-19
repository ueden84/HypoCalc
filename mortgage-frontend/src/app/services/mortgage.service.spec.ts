import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MortgageService } from './mortgage.service';
import { MortgageRequest, MortgageResult } from '../models/mortgage.model';

describe('MortgageService', () => {
  let service: MortgageService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MortgageService]
    });
    service = TestBed.inject(MortgageService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should calculate mortgage', () => {
    const mockRequest: MortgageRequest = {
      principal: 300000,
      annualRatePercent: 5.0,
      years: 30
    };

    const mockResult: MortgageResult = {
      monthlyPayment: 1610.46,
      totalPaid: 579765.60,
      totalInterest: 279765.60
    };

    service.calculateMortgage(mockRequest).subscribe(result => {
      expect(result).toEqual(mockResult);
    });

    const req = httpMock.expectOne('/api/mortgage/calculate');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRequest);
    req.flush(mockResult);
  });
});
