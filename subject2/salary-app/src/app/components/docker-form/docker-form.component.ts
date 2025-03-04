import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DockerService } from '../../services/docker.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-docker-form',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './docker-form.component.html',
  styleUrls: ['./docker-form.component.scss']
  /*
  template: `
    <div class="container mt-4">
      <h2>Add New Docker</h2>
      <form [formGroup]="dockerForm" (ngSubmit)="onSubmit()">
        <div class="mb-3">
          <label for="qualification" class="form-label">Qualification</label>
          <input type="text" class="form-control" id="qualification" formControlName="qualification">
        </div>
        
        <div class="mb-3">
          <label for="experienceYears" class="form-label">Years of Experience</label>
          <input type="number" class="form-control" id="experienceYears" formControlName="experienceYears">
        </div>
        
        <div class="mb-3">
          <label for="baseSalary" class="form-label">Base Salary</label>
          <input type="number" class="form-control" id="baseSalary" formControlName="baseSalary">
        </div>
        
        <div class="mb-3 form-check">
          <input type="checkbox" class="form-check-input" id="active" formControlName="active">
          <label class="form-check-label" for="active">Active</label>
        </div>
        
        <button type="submit" class="btn btn-primary" [disabled]="!dockerForm.valid">Submit</button>
      </form>
    </div>
    `*/
})
export class DockerFormComponent {
  dockerForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dockerService: DockerService,
    private router: Router
  ) {
    this.dockerForm = this.fb.group({
      qualification: ['', Validators.required],
      experienceYears: [0, [Validators.required, Validators.min(0)]],
      baseSalary: [0, [Validators.required, Validators.min(0)]],
      active: [true]
    });
  }

  onSubmit() {
    if (this.dockerForm.valid) {
      this.dockerService.createDocker(this.dockerForm.value).subscribe({
        next: () => {
          this.router.navigate(['/dockers']);
        },
        error: (error) => {
          console.error('Error creating docker:', error);
        }
      });
    }
  }
} 