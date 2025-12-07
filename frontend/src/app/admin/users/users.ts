// users-management.component.ts
import { Component, computed, Input, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Author } from '../../shared/models';
import { PostService } from '../../posts/authpost';
import { ConfirmationPopupComponent } from '../../shared/confirmation/confirmation';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';  // ← Add MatDialog import

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
  filterStatus = signal<'all' | 'active' | 'suspended'>('all');
  loading = signal(false);
  loadingusers = false;
  lastUserid = this.users().length > 0 ? this.users()[this.users().length - 1].id : 1;

  constructor(
    private psr: PostService,
    private dialog: MatDialog  // ← Add this
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

  onFilterChange(status: 'all' | 'active' | 'suspended') {
    this.filterStatus.set(status);
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