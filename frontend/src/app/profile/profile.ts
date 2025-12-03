import { CommonModule } from '@angular/common';
import { Component, Input, signal } from '@angular/core';
import { Profile, getAvatarUrl, getMediaUrl } from '../shared/models'
import { PostService } from '../posts/authpost';
import { PostItemComponent } from "../posts/post-item/post-item";
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss'],
  imports: [CommonModule, PostItemComponent]
})
export class ProfileComponent {
  profile = signal<Profile>({
    user: { username: '', avatar: '', status: false, id: 0 },
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
  async toggleFollow() {
    let username = this.profile().user.username;
    const rsp = await this.psr.follow(username);
    if (rsp) {
      this.isFollowing.set(!this.isFollowing());

    }
  }
  constructor(private psr: PostService, private auth:AuthService, private route: ActivatedRoute, private router: Router) { }
  async ngOnInit() {
    let username = (await this.auth.whoami()).username

    let profile = await this.psr.fetchProfile(username);
    if (profile.error.length > 0 && profile.error[0] == "InvalidInfo") {
      this.router.navigate(['feed'])
    }

    this.isFollowing.set(profile.profile.followed())
    let lastindex = profile.profile.posts().length - 1;
    this.lastid = lastindex > 0 ? profile.profile.posts()[lastindex].id : 0;
    this.profile.set(profile.profile);
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
}
