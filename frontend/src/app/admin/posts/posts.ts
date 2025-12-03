import { CommonModule } from '@angular/common';
import { Component, Input, OnInit, signal, WritableSignal } from '@angular/core';
import { AdminPost, Post, PostStatus, Report, getAvatarUrl } from '../../shared/models';
import { Router } from '@angular/router';
import { PostService } from '../../posts/authpost';
import { log } from 'console';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.html',
  styleUrls: ['./posts.scss'],
  imports: [CommonModule]
})
export class PostsComponent implements OnInit {
  @Input() reports: WritableSignal<Report[]> = signal([]);
  @Input() posts: WritableSignal<AdminPost[]> = signal([]);
  loadingpost: boolean = false;
  loadingreport: boolean = false;

  lastPostid: number = 0;
  lastReportid: number = 0;
  darkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
  getAvatarUrl = getAvatarUrl;
  constructor(private router: Router, private psr: PostService) {
    console.log(this.posts().length);

  }

  ngOnInit(): void {
  }
  onEdit(id: number) {
    this.router.navigate([`post/edit/${id}`]);
  }
  async onDelete(id: number) {
    const rsp = await this.psr.DeletePost(id);
  }
  getStatusColor(status: PostStatus): string {
    const colors: Record<PostStatus, string> = {
      Active: this.darkMode ? 'status-Active-dark' : 'status-Active-light',
      Hidden: this.darkMode ? 'status-Hidden-dark' : 'status-Hidden-light'
    };
    return colors[status];
  }
  async onToggleStatus(post: AdminPost) {
    const rsp = await this.psr.UpdatePostStatus(post.id);
    post.status.set(rsp ? !post.status() : post.status())
  }
  async OnToggleReport(report: Report) {
    const rsp = await this.psr.UpdateReportStatus(report.id);
    report.state.set(rsp ? !report.state() : report.state())
  }
  async Postinfinitescroll(ev: Event) {
    if (this.loadingpost) return;
    const e = ev.target as HTMLElement;
    if (e.scrollHeight - e.scrollTop <= e.clientHeight + 20) {
      this.loadingpost = true;
      this.lastPostid = this.posts().length > 0 ? this.posts()[this.posts().length - 1].id : 1
      const posts = await this.psr.getAdminposts(this.lastPostid);
      this.posts.update(p => [...p, ...posts]);
      this.lastPostid = posts.length > 0 ? posts[posts.length - 1].id : 1;
      this.loadingpost = false;
    }
  }
  async Reportinfinitescroll(ev: Event) {
    if (this.loadingpost) return;
    const e = ev.target as HTMLElement;
    if (e.scrollHeight - e.scrollTop <= e.clientHeight + 20) {
      this.loadingreport = true;
      this.lastReportid = this.reports().length > 0 ? this.reports()[this.reports().length - 1].id : 1
      const reports = await this.psr.getAdminreports(this.lastReportid);
      console.log(reports);
      
      this.reports.update(p => [...p, ...reports]);
      this.lastReportid = reports.length > 0 ? reports[reports.length - 1].id : 1;
      this.loadingreport = false;
    }
  }
}
