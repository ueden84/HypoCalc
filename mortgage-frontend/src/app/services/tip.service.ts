import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TipRequest, TipResponse } from '../models/mortgage.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TipService {
  private readonly apiUrl = `${environment.apiUrl}/api/ai/tips`;

  constructor(private http: HttpClient) {}

  getTip(request: TipRequest): Observable<TipResponse> {
    return this.http.post<TipResponse>(this.apiUrl, request);
  }
}
