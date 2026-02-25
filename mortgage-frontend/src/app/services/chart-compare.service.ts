import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChartCompareRequest, ChartCompareResponse } from '../models/mortgage.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChartCompareService {
  private readonly apiUrl = `${environment.apiUrl}/api/chart/compare`;

  constructor(private http: HttpClient) {}

  compareChart(request: ChartCompareRequest): Observable<ChartCompareResponse> {
    return this.http.post<ChartCompareResponse>(this.apiUrl, request);
  }
}
