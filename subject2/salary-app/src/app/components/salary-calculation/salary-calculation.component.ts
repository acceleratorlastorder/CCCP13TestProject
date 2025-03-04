import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SalaryCalculationService } from '../../services/salary-calculation.service';
import { SalaryCalculation } from '../../models/salary-calculation.model';
import { SalaryStatistics } from '../../models/salary-statistics.model';

@Component({
  selector: 'app-salary-calculation',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './salary-calculation.component.html',
  styleUrls: ['./salary-calculation.component.scss']
})
export class SalaryCalculationComponent implements OnInit {
  calculations: SalaryCalculation[] = [];
  statistics: SalaryStatistics | null = null;
  searchTerm: string = '';
  filteredCalculations: SalaryCalculation[] = [];

  constructor(private salaryCalculationService: SalaryCalculationService) {}

  ngOnInit() {
    this.loadData();
    this.loadStatistics();
  }

  loadData() {
    this.salaryCalculationService.getAllCalculations().subscribe({
      next: (data) => {
        this.calculations = data;
        this.filteredCalculations = data;
      },
      error: (error) => {
        console.error('Error loading calculations:', error);
      }
    });
  }

  loadStatistics() {
    this.salaryCalculationService.getStatistics().subscribe({
      next: (stats) => {
        this.statistics = stats;
      },
      error: (error) => {
        console.error('Error loading statistics:', error);
      }
    });
  }

  filterCalculations() {
    if (!this.searchTerm) {
      this.filteredCalculations = this.calculations;
      return;
    }

    this.filteredCalculations = this.calculations.filter(calc => 
      calc.docker?.qualification?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      calc.workShift?.type?.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  refreshData() {
    this.loadData();
    this.loadStatistics();
  }

  exportData() {
    // TODO: Implement export functionality
    console.log('Export functionality to be implemented');
  }

  viewDetails(id: number | undefined) {
    if (id) {
      // TODO: Implement view details functionality
      console.log('View details for calculation:', id);
    }
  }

  printCalculation(id: number | undefined) {
    if (id) {
      // TODO: Implement print functionality
      console.log('Print calculation:', id);
    }
  }

  calculateAllSalaries() {
    if (confirm('Are you sure you want to calculate salaries for all work shifts?')) {
      this.salaryCalculationService.calculateAllSalaries().subscribe({
        next: () => {
          this.loadData();
          this.loadStatistics();
        },
        error: (error: any) => {
          console.error('Error calculating salaries:', error);
          alert('Error calculating salaries. Please try again.');
        }
      });
    }
  }
}