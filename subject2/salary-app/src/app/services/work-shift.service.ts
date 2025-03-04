import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WorkShift } from '../models/work-shift.model';
import { ApiConfigService } from './api-config.service';

@Injectable({
  providedIn: 'root'
})
export class WorkShiftService {
  constructor(
    private http: HttpClient,
    private apiConfig: ApiConfigService
  ) { }

  getWorkShifts(): Observable<WorkShift[]> {
    return this.http.get<WorkShift[]>(this.apiConfig.getWorkShiftUrl());
  }

  getWorkShift(id: number): Observable<WorkShift> {
    return this.http.get<WorkShift>(`${this.apiConfig.getWorkShiftUrl()}/${id}`);
  }

  createWorkShift(workShift: WorkShift): Observable<WorkShift> {
    return this.http.post<WorkShift>(this.apiConfig.getWorkShiftUrl(), workShift);
  }

  updateWorkShift(id: number, workShift: WorkShift): Observable<WorkShift> {
    return this.http.put<WorkShift>(`${this.apiConfig.getWorkShiftUrl()}/${id}`, workShift);
  }

  deleteWorkShift(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiConfig.getWorkShiftUrl()}/${id}`);
  }

  validateWorkShift(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiConfig.getWorkShiftUrl()}/${id}/validate`, {});
  }
} 