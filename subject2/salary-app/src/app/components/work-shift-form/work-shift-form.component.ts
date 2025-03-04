import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DockerService } from '../../services/docker.service';
import { WorkShiftService } from '../../services/work-shift.service';
import { Docker } from '../../models/docker.model';
import { Router } from '@angular/router';
import { WorkShift } from '../../models/work-shift.model';

@Component({
  selector: 'app-work-shift-form',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],  
  templateUrl: './work-shift-form.component.html',
  styleUrls: ['./work-shift-form.component.scss']
})
export class WorkShiftFormComponent implements OnInit {
  workShiftForm: FormGroup;
  dockers: Docker[] = [];

  constructor(
    private fb: FormBuilder,
    private dockerService: DockerService,
    private workShiftService: WorkShiftService,
    private router: Router
  ) {
    this.workShiftForm = this.fb.group({
      dockerId: ['', Validators.required],
      type: ['jour', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      weekend: [false],
      holiday: [false]
    });
  }

  ngOnInit() {
    this.dockerService.getDockers().subscribe({
      next: (dockers) => {
        this.dockers = dockers;
      },
      error: (error) => {
        console.error('Error loading dockers:', error);
      }
    });
  }

  onSubmit() {
    if (this.workShiftForm.valid) {
      const formValue = this.workShiftForm.value;
      const workShift: WorkShift = {
        docker: { id: formValue.dockerId },
        type: formValue.type,
        startTime: formValue.startTime,
        endTime: formValue.endTime,
        weekend: formValue.weekend,
        holiday: formValue.holiday
      };
      
      this.workShiftService.createWorkShift(workShift).subscribe({
        next: () => {
          this.router.navigate(['/work-shifts']);
        },
        error: (error) => {
          console.error('Error creating work shift:', error);
        }
      });
    }
  }
} 