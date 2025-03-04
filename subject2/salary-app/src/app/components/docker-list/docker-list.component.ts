import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DockerService } from '../../services/docker.service';
import { Docker } from '../../models/docker.model';

@Component({
  selector: 'app-docker-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './docker-list.component.html',
  styleUrls: ['./docker-list.component.scss']
})
export class DockerListComponent implements OnInit {
  dockers: Docker[] = [];

  constructor(private dockerService: DockerService) {}

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

  deleteDocker(id: number) {
    if (confirm('Are you sure you want to delete this docker?')) {
      this.dockerService.deleteDocker(id).subscribe({
        next: () => {
          this.dockers = this.dockers.filter(docker => docker.id !== id);
        },
        error: (error) => {
          console.error('Error deleting docker:', error);
        }
      });
    }
  }
} 