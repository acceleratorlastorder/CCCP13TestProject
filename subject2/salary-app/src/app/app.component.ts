import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavBarComponent],
  template: `
    <app-nav-bar></app-nav-bar>
    <div class="app-container">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: calc(100vh - 60px);
      background: #f8f9fa;
    }
  `]
})
export class AppComponent {
  title = 'salary-app';
}
