// dashboard.component.ts
import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Dashboard } from '../../shared/models';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class DashboardComponent implements OnInit {
  @Input() dashboard: Dashboard = {} as Dashboard;
  loading = false;
  ngOnInit() {
  }
  getAvatarUrl(avatar: string): string {
    return `http://localhost:8080/avatars/${avatar}`;
  }
  getPercentage(value: number, total: number): number {
    return total > 0 ? (value / total) * 100 : 0;
  }
}