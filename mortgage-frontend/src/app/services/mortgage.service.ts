import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MortgageRequest, MortgageResult } from '../models/mortgage.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MortgageService {
  private readonly apiUrl = `${environment.apiUrl}/api/mortgage/calculate`;

  constructor(private http: HttpClient) {}

  calculateMortgage(request: MortgageRequest): Observable<MortgageResult> {
    return this.http.post<MortgageResult>(this.apiUrl, request);
  }
}
