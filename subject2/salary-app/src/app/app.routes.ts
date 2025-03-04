import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SalaryCalculationComponent } from './components/salary-calculation/salary-calculation.component';
import { DockerFormComponent } from './components/docker-form/docker-form.component';
import { WorkShiftFormComponent } from './components/work-shift-form/work-shift-form.component';
import { DockerListComponent } from './components/docker-list/docker-list.component';
import { WorkShiftListComponent } from './components/work-shift-list/work-shift-list.component';

export const routes: Routes = [
  { path: '', redirectTo: '/salary-calculations', pathMatch: 'full' },
  { path: 'salary-calculations', component: SalaryCalculationComponent },
  { path: 'dockers', component: DockerListComponent },
  { path: 'dockers/new', component: DockerFormComponent },
  { path: 'work-shifts', component: WorkShiftListComponent },
  { path: 'work-shifts/new', component: WorkShiftFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }