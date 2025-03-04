import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SalaryCalculation {
  id: number;
  docker: any; // refine as needed
  workShift: any;
  workSite: any;
  cargoType: any;
  baseSalary: number;
  totalBonus: number;
  totalSalary: number;
  calculationDate: string;
  // add other properties as needed
}

@Injectable({
  providedIn: 'root'
})
export class SalaryService {
  // Replace with your backend URL
  private apiUrl = 'http://localhost:8888/api/salary-calculations';

  constructor(private http: HttpClient) {}

  getSalaryCalculations(): Observable<SalaryCalculation[]> {
    return this.http.get<SalaryCalculation[]>(this.apiUrl);
  }

  getSalaryCalculationById(id: number): Observable<SalaryCalculation> {
    return this.http.get<SalaryCalculation>(`${this.apiUrl}/${id}`);
  }

  // Other methods (create, update, delete) can be added here
}