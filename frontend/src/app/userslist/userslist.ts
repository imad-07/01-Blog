import { Component, computed, OnInit, signal, WritableSignal, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Author } from '../shared/models';
import { PostService } from '../posts/authpost';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './userslist.html',
  styleUrls: ['./userslist.scss'],
})
export class UserslistComponent implements OnInit {
  @ViewChild('usersList', { static: false }) usersListElement!: ElementRef<HTMLElement>;

  constructor(private psr: PostService) { }

  lastUserid = 0;
  loadingusers = signal(false);
  followedUsers = new Set<number>();
  users: WritableSignal<Author[]> = signal([]);
  private previousScrollHeight = 0;

  async ngOnInit() {
    this.loadingusers.set(true);
    const users = await this.psr.getUsersList(this.lastUserid);
    console.log(users);
    this.users.update(p => [...p, ...users]);
    this.lastUserid = users.length > 0 ? users[users.length - 1].id : 1;
    this.loadingusers.set(false);
  }

  async toggleFollow(user: Author) {
    // Optimistic toggle
    user.status = !user.status;
    this.users.set([...this.users()]); // Re-emit signal to trigger UI update

    try {
      const rsp = await this.psr.follow(user.username);
      if (!rsp) {
        // Revert if failed
        user.status = !user.status;
        this.users.set([...this.users()]);
        console.error('Failed to toggle follow status');
      }
    } catch (err) {
      // Revert if errored
      user.status = !user.status;
      this.users.set([...this.users()]);
      console.error('Error toggling follow status:', err);
    }
  }


  getAvatarUrl(avatar: string): string {
    return `http://localhost:8080/avatars/${avatar}`;
  }

  async onUserInfiniteScroll(ev: any) {
    if (this.loadingusers()) return;

    const e = ev.target as HTMLElement;

    if (e.scrollHeight - e.scrollTop <= e.clientHeight + 20) {
      this.loadingusers.set(true);

      // Store the scroll height before loading
      this.previousScrollHeight = e.scrollHeight;

      this.lastUserid = this.users().length > 0 ? this.users()[this.users().length - 1].id : 1;
      const users = await this.psr.getUsersList(this.lastUserid);

      if (users.length > 0) {
        this.users.update(p => [...p, ...users]);
        this.lastUserid = users[users.length - 1].id;

        // Maintain scroll position by calculating the new height difference
        setTimeout(() => {
          const newScrollHeight = e.scrollHeight;
          const heightDifference = newScrollHeight - this.previousScrollHeight;
          e.scrollTop += heightDifference;
        }, 0);
      }

      this.loadingusers.set(false);
    }
  }

}