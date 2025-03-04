import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiConfigService {
  private baseUrl = environment.apiUrl;

  getDockerUrl(): string {
    return `${this.baseUrl}/dockers`;
  }

  getWorkShiftUrl(): string {
    return `${this.baseUrl}/work-shifts`;
  }

  getSalaryCalculationUrl(): string {
    return `${this.baseUrl}/salary-calculations`;
  }
} 