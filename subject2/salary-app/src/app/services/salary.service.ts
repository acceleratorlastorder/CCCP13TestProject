import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiConfigService } from './api-config.service';

export interface SalaryCalculation {
  id: number;
  docker: any;
  workShift: any;
  workSite: any;
  cargoType: any;
  baseSalary: number;
  totalBonus: number;
  totalSalary: number;
  calculationDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class SalaryService {
  constructor(
    private http: HttpClient,
    private apiConfig: ApiConfigService
  ) { }

  getSalaryCalculations(): Observable<SalaryCalculation[]> {
    return this.http.get<SalaryCalculation[]>(this.apiConfig.getSalaryCalculationUrl());
  }

  getSalaryCalculationById(id: number): Observable<SalaryCalculation> {
    return this.http.get<SalaryCalculation>(`${this.apiConfig.getSalaryCalculationUrl()}/${id}`);
  }
} 