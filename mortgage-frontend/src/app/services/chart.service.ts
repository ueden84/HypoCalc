import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChartRequest, ChartResponse } from '../models/mortgage.model';

@Injectable({
  providedIn: 'root'
})
export class ChartService {
  private apiUrl = '/api/chart/calculate';

  constructor(private http: HttpClient) {}

  calculateChart(request: ChartRequest): Observable<ChartResponse> {
    return this.http.post<ChartResponse>(this.apiUrl, request);
  }
}
