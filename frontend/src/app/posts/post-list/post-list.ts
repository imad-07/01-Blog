import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostItemComponent } from '../post-item/post-item';
import { PostService } from '../authpost';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { Post } from '../../shared/models';



@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [CommonModule, PostItemComponent],
  templateUrl: './post-list.html',
  styleUrls: ['./post-list.scss']
})
export class PostListComponent implements OnInit {
  posts: WritableSignal<Post[]> = signal([]);
  lastid = 0;
  loading = false;
  user = '';
  backendUrl = 'http://localhost:8080/';

  constructor(private psr: PostService, private router: Router, private auth: AuthService) { }
  async ngOnInit() {
    this.loading = true;
    this.user = (await this.auth.whoami()).username;
    const posts = await this.psr.getposts(this.lastid);
    this.posts.set(posts);
    let psize = posts.length;
    this.lastid = psize > 0 ? posts[psize - 1].id : 0;
    this.loading = false;
  }

  async infinitescroll(ev: Event) {
    if (this.loading) return;
    const e = ev.target as HTMLElement;
    if (e.scrollHeight - e.scrollTop <= e.clientHeight + 100) {
      this.loading = true;
      const posts = await this.psr.getposts(this.lastid);
      this.posts.update(p => [...p, ...posts]);
      this.lastid = posts[posts.length - 1].id;
      this.loading = false;
    }
  }
}
