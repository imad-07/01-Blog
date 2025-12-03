// dashboard.component.ts
import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface Post {
  id: number;
  title: string;
  author: {
    name: string;
    avatar: string;
    username: string;
  };
  content: string;
  media?: {
    type: string;
    url: string;
  };
  timestamp: Date;
  likes: number;
  comments: any;
  liked: boolean;
  showcmts?: boolean;
  cmts: number;
  status: string;
}

export interface Author {
  avatar: string;
  username: string;
  name?: string;
}

export interface Dashboard {
  totalactiveusers: number;
  totalsuspendedusers: number;
  totalusers: number;
  totalposts: number;
  totalactiveposts: number;
  totalsuspendedposts: number;
  totalcomments: number;
  totalpendingreports: number;
  totalhandledreports: number;
  totalreports: number;
  star: Author | null;
  mostlikedpost: Post | null;
  latestusers: Author[];
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class DashboardComponent implements OnInit {
  @Input() dashboard: Dashboard | null = null;
  loading = false;

  ngOnInit() {
  }
  getAvatarUrl(avatar: string): string {
    return `http://localhost:8080/avatars/${avatar}`;
  }
  getPercentage(value: number, total: number): number {
    return total > 0 ? (value / total) * 100 : 0;
  }

  getInitials(name: string | undefined): string {
    if (!name) return '?';
    return name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase();
  }
}