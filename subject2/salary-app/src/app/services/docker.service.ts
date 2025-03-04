import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Docker } from '../models/docker.model';
import { ApiConfigService } from './api-config.service';

@Injectable({
  providedIn: 'root'
})
export class DockerService {
  constructor(
    private http: HttpClient,
    private apiConfig: ApiConfigService
  ) { }

  getDockers(): Observable<Docker[]> {
    return this.http.get<Docker[]>(this.apiConfig.getDockerUrl());
  }

  getDocker(id: number): Observable<Docker> {
    return this.http.get<Docker>(`${this.apiConfig.getDockerUrl()}/${id}`);
  }

  createDocker(docker: Docker): Observable<Docker> {
    return this.http.post<Docker>(this.apiConfig.getDockerUrl(), docker);
  }

  updateDocker(id: number, docker: Docker): Observable<Docker> {
    return this.http.put<Docker>(`${this.apiConfig.getDockerUrl()}/${id}`, docker);
  }

  deleteDocker(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiConfig.getDockerUrl()}/${id}`);
  }
} 