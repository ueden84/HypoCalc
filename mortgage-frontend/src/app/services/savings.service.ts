import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SavingsRequest, SavingsResult } from '../models/mortgage.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SavingsService {
  private readonly apiUrl = `${environment.apiUrl}/api/savings/calculate`;

  constructor(private http: HttpClient) {}

  calculateSavings(request: SavingsRequest): Observable<SavingsResult> {
    return this.http.post<SavingsResult>(this.apiUrl, request);
  }
}
