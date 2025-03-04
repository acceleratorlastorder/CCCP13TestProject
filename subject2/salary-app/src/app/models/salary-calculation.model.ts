import { Docker } from './docker.model';
import { WorkShift } from './work-shift.model';

export interface SalaryCalculation {
  id?: number;
  docker?: Docker;
  workShift?: WorkShift;
  baseSalary?: number;
  totalBonus?: number;
  totalSalary?: number;
  calculationDate?: Date;
} 