import { Component, OnInit, signal } from '@angular/core';
import { DashboardComponent } from "./dashboard/dashboard";
import { PostsComponent } from "./posts/posts";
import { UsersComponent } from "./users/users";
import { CommonModule } from '@angular/common';
import { PostService } from '../posts/authpost';
import { AdminPost, AdminResponse, Author, Report } from '../shared/models';
import { ProfileComponent } from "../profile/profile";
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.html',
  styleUrls: ['./admin.scss'],
  imports: [DashboardComponent, PostsComponent, CommonModule, UsersComponent, ProfileComponent, RouterLink]
})
export class Admin implements OnInit {
  darkMode = false;
  admin: AdminResponse = {
    reports: [],
    posts: [],
    dashboard: {
      totalactiveusers: 0,
      totalsuspendedusers: 0,
      totalusers: 0,
      totalposts: 0,
      totalactiveposts: 0,
      totalsuspendedposts: 0,
      totalcomments: 0,
      totalpendingreports: 0,
      totalhandledreports: 0,
      totalreports: 0,
      star: null,
      mostlikedpost: null,
      latestusers: [],
    },
    users: []
  };
  Posts = signal<AdminPost[]>([]);
  Reports = signal<Report[]>([]);
  Users = signal<Author[]>([]);
  currentView: 'dashboard' | 'posts' | 'users' | 'admin'= 'admin';
  constructor(private psr: PostService) { }
  async ngOnInit() {
    this.admin = await this.psr.GetAdmin()
    this.Posts.set(this.admin.posts)
    this.Reports.set(this.admin.reports)
    this.Users.set(this.admin.users)
    const prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    this.darkMode = prefersDark;

    // Optional: Listen for changes dynamically
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
      this.darkMode = e.matches;
    });
  }
}
