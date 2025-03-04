import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { WorkShiftService } from '../../services/work-shift.service';
import { WorkShift } from '../../models/work-shift.model';

@Component({
  selector: 'app-work-shift-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './work-shift-list.component.html',
  styleUrls: ['./work-shift-list.component.scss']
})
export class WorkShiftListComponent implements OnInit {
  workShifts: WorkShift[] = [];

  constructor(private workShiftService: WorkShiftService) {}

  ngOnInit() {
    this.loadWorkShifts();
  }

  loadWorkShifts() {
    this.workShiftService.getWorkShifts().subscribe({
      next: (shifts: WorkShift[]) => {
        this.workShifts = shifts;
      },
      error: (error: any) => {
        console.error('Error loading work shifts:', error);
      }
    });
  }

  deleteWorkShift(id: number | undefined) {
    if (id && confirm('Êtes-vous sûr de vouloir supprimer ce shift ?')) {
      this.workShiftService.deleteWorkShift(id).subscribe({
        next: () => {
          this.loadWorkShifts();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
        }
      });
    }
  }

  validateWorkShift(id: number | undefined) {
    if (id && confirm('Êtes-vous sûr de vouloir valider ce shift ?')) {
      this.workShiftService.validateWorkShift(id).subscribe({
        next: () => {
          this.loadWorkShifts();
        },
        error: (error) => {
          console.error('Erreur lors de la validation:', error);
        }
      });
    }
  }
} 