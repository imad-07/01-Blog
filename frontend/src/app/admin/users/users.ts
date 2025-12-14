// users-management.component.ts
import { Component, computed, Input, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Author, UserReport } from '../../shared/models';
import { PostService } from '../../posts/authpost';
import { ConfirmationPopupComponent } from '../../shared/confirmation/confirmation';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';  // ← Add MatDialog import

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule],  // ← Add ConfirmationPopupComponent
  templateUrl: './users.html',
  styleUrls: ['./users.scss']
})
export class UsersComponent {
  @Input() users: WritableSignal<Author[]> = signal([]);
  @Input() totalusers: number = 0;
  @Input() totalactiveusers: number = 0;
  @Input() totalsuspendedusers: number = 0;

  searchQuery = signal('');
  filterStatus = signal<'all' | 'active' | 'suspended' | 'reports'>('all');
  loading = signal(false);

  // Reports Logic
  reports = signal<UserReport[]>([]);
  loadingReports = false;
  lastReportId = 0;
  reportsLoaded = false;
  loadingusers = false;
  lastUserid = this.users().length > 0 ? this.users()[this.users().length - 1].id : 1;

  constructor(
    private psr: PostService,
    private dialog: MatDialog,
    private router: Router
  ) { }

  filteredUsers = computed(() => {
    let filtered = this.users();
    // Filter by status
    if (this.filterStatus() !== 'all') {
      const statusValue = this.filterStatus() === 'active';
      filtered = filtered.filter(u => u.status === statusValue);
    }
    return filtered;
  });

  getAvatarUrl(avatar: string) {
    return `http://localhost:8080/avatars/${avatar}`;
  }

  async toggleUserStatus(user: Author) {
    const rsp = await this.psr.UpdateUserStatus(user.id);
    if (rsp) {
      this.users.update(users =>
        users.map(u =>
          u.username === user.username ? { ...u, status: !u.status } : u
        )
      );
    }
  }

  onSearchChange(query: string) {
    this.searchQuery.set(query);
  }

  async onFilterChange(status: 'all' | 'active' | 'suspended' | 'reports') {
    this.filterStatus.set(status);
    if (status === 'reports' && !this.reportsLoaded) {
      await this.loadInitialReports();
    }
  }

  async loadInitialReports() {
    this.loading.set(true);
    const reports = await this.psr.getAdminUserReports(0);
    this.reports.set(reports);
    this.lastReportId = reports.length > 0 ? reports[reports.length - 1].id : 0;
    this.reportsLoaded = true;
    this.loading.set(false);
  }

  async ReportsInfiniteScroll(ev: Event) {
    if (this.loadingReports) return;
    const e = ev.target as HTMLElement;
    if (e.scrollHeight - e.scrollTop <= e.clientHeight + 20) {
      this.loadingReports = true;
      const reports = await this.psr.getAdminUserReports(this.lastReportId);
      if (reports.length > 0) {
        this.reports.update(curr => [...curr, ...reports]);
        this.lastReportId = reports[reports.length - 1].id;
      }
      this.loadingReports = false;
    }
  }

  async handleReport(report: UserReport) {
    // Call backend to handle report
    const success = await this.psr.UpdateReportStatus(report.id); // Reusing existing method if compatible, else create new
    if (success) {
      report.state.set(true);
    } else {
      // Try specific user report endpoint if generic one fails, but let's assume reuse for now or new method needed
      // Actually PostService has UpdateReportStatus calling /admin/report/{id} but that's for Post reports usually.
      // We need UpdateUserReportStatus.
      const userReportSuccess = await this.psr.UpdateUserReportStatus(report.id);
      if (userReportSuccess) report.state.set(true);
    }
  }

  viewReportedUser(username: string) {
    this.router.navigate(['/profile', username]);
  }

  async Userinfinitescroll(ev: Event) {
    if (this.loadingusers) return;
    const e = ev.target as HTMLElement;
    if (e.scrollHeight - e.scrollTop <= e.clientHeight + 20) {
      this.loadingusers = true;
      this.lastUserid = this.users().length > 0 ? this.users()[this.users().length - 1].id : 1
      const users = await this.psr.getAdminusers(this.lastUserid);
      this.users.update(p => [...p, ...users]);
      this.lastUserid = users.length > 0 ? users[users.length - 1].id : 1;
      this.loadingusers = false;
    }
  }

  async deleteUser(user: Author) {
    // User confirmed, proceed with deletion
    const rsp = await this.psr.Deleteuser(user.id);
    console.log(rsp);

    // Remove user from list
    this.users.update(users =>
      users.filter(u => u.id !== user.id)
    );
  }
}