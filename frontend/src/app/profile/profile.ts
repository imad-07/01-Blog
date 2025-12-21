import { CommonModule } from '@angular/common';
import { Component, Input, signal } from '@angular/core';
import { Profile, getAvatarUrl, getMediaUrl } from '../shared/models'
import { PostService } from '../posts/authpost';
import { PostItemComponent } from "../posts/post-item/post-item";
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { ReportService } from '../services/report.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss'],
  imports: [CommonModule, PostItemComponent]
})
export class ProfileComponent {
  profile = signal<Profile>({
    user: { username: '', avatar: '', status: false, id: 0, role: 'USER' },
    followers: [],
    following: [],
    posts: signal([]),
    followed: signal(false)
  });
  lastid = 0;
  loading = false;
  showFollowers = false;
  showFollowing = false;
  isFollowing = signal(false);
  username = "";

  // Report popup properties
  reportReasons = ['SPAM', 'HARASSMENT', 'INAPPROPRIATE', 'COPYRIGHT', 'MISINFORMATION', 'OTHER'];
  showReportPopup = signal(false);

  async toggleFollow() {
    let username = this.profile().user.username;
    const rsp = await this.psr.follow(username);
    if (rsp) {
      this.isFollowing.set(!this.isFollowing());

      let followers = this.profile().followers;
      if (this.isFollowing()) {
        followers = [...followers, this.currentUserObj];
      } else {
        followers = followers.filter(f => f.username !== this.currentUserObj.username);
      }
      this.profile.update(p => ({ ...p, followers }));
    }
  }
  currentUserObj: any = {};
  currentUser = signal("");

  constructor(private psr: PostService, private auth: AuthService, private route: ActivatedRoute, private router: Router, private reportService: ReportService) { }
  async ngOnInit() {
    // Get current user for self-report check
    try {
      const user = await this.auth.whoami();
      this.currentUserObj = user;
      this.currentUser.set(user.username);
    } catch (e) {
      // User might not be logged in or token invalid
    }

    this.route.paramMap.subscribe(async params => {
      this.username = params.get('username') || '';
      let profile = await this.psr.fetchProfile(this.username);
      if (profile.error.length > 0 && profile.error[0] == "InvalidInfo") {
        this.router.navigate(['feed'])
      }

      this.isFollowing.set(profile.profile.followed())
      let lastindex = profile.profile.posts().length - 1;
      this.lastid = lastindex > 0 ? profile.profile.posts()[lastindex].id : 0;
      this.profile.set(profile.profile);
    });
  }
  toggleFollowers() {
    this.showFollowers = !this.showFollowers;
  }

  toggleFollowing() {
    this.showFollowing = !this.showFollowing;
  }
  getAvatarUrl(avatar: string) {
    if (avatar == "") {
      return ""
    }
    return `http://localhost:8080/avatars/${avatar}`;
  }
  getMediaUrl(path: string) {
    return `http://localhost:8080/files/posts/${path}`;
  }
  async getmyposts(ev: Event) {
    if (this.loading) return;
    const e = ev.target as HTMLElement;
    if (e.scrollHeight - e.scrollTop <= e.clientHeight + 100) {
      this.loading = true;
      const posts = await this.psr.getmyposts(this.lastid);
      this.profile().posts.update(p => [...p, ...posts]);
      this.lastid = posts[posts.length - 1].id;
      this.loading = false;
    }
  }

  /**
   * Open report popup for this user
   */
  openReportPopup() {
    this.showReportPopup.set(true);
  }

  closeReportPopup() {
    this.showReportPopup.set(false);
  }

  submitUserReport(reason: string) {
    this.closeReportPopup();
    this.reportService.reportUser(this.profile().user.id, reason).subscribe({
      next: () => {
        // Success handled by interceptor or silent
      },
      error: () => {
        // Error handled by interceptor
      }
    });
  }

  handleDelete(postId: number) {
    this.profile().posts.update(p => p.filter(post => post.id !== postId));
  }
}
