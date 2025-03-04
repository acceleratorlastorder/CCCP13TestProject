import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SalaryCalculation } from '../models/salary-calculation.model';
import { SalaryStatistics } from '../models/salary-statistics.model';
import { ApiConfigService } from './api-config.service';

@Injectable({
  providedIn: 'root'
})
export class SalaryCalculationService {
  constructor(
    private http: HttpClient,
    private apiConfig: ApiConfigService
  ) { }

  getAllCalculations(): Observable<SalaryCalculation[]> {
    return this.http.get<SalaryCalculation[]>(this.apiConfig.getSalaryCalculationUrl());
  }

  getCalculationById(id: number): Observable<SalaryCalculation> {
    return this.http.get<SalaryCalculation>(`${this.apiConfig.getSalaryCalculationUrl()}/${id}`);
  }

  getDockerCalculations(dockerId: number): Observable<SalaryCalculation[]> {
    return this.http.get<SalaryCalculation[]>(`${this.apiConfig.getSalaryCalculationUrl()}/docker/${dockerId}`);
  }

  getStatistics(): Observable<SalaryStatistics> {
    return this.http.get<SalaryStatistics>(`${this.apiConfig.getSalaryCalculationUrl()}/statistics`);
  }

  createCalculation(calculation: SalaryCalculation): Observable<SalaryCalculation> {
    return this.http.post<SalaryCalculation>(this.apiConfig.getSalaryCalculationUrl(), calculation);
  }

  calculateAllSalaries(): Observable<void> {
    return this.http.post<void>(`${this.apiConfig.getSalaryCalculationUrl()}/calculate-all`, {});
  }
} 