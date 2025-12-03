import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PostService } from '../authpost';
import { Post } from '../../shared/models';
import { PostCommentsComponent } from '../post-comments/post-comments';

@Component({
  selector: 'app-post-item',
  standalone: true,
  imports: [CommonModule, PostCommentsComponent],
  templateUrl: './post-item.html',
  styleUrls: ['./post-item.scss']
})
export class PostItemComponent {
  @Input() post!: Post;
  @Input() backendUrl!: string;
  @Input() currentUser!: string;

  constructor(private psr: PostService, private router: Router) {
  }
  // post-item.component.ts
  reportReasons = ['SPAM', 'HARASSMENT', 'INAPPROPRIATE', 'COPYRIGHT', 'MISINFORMATION', 'OTHER'];

  showReportPopup = false;
  selectedPostId: number | null = null;

  openReportPopup(postId: number) {
    this.selectedPostId = postId;
    this.showReportPopup = true;
  }

  closeReportPopup() {
    this.showReportPopup = false;
    this.selectedPostId = null;
  }

  async submitReport(reason: string) {
    let resp = await this.psr.report(this.post.id, reason);
    console.log(resp);
    
  }
  async onLike() {
    let rsp = await this.psr.like(this.post.id);
    if (rsp){
    this.post.liked = !this.post.liked;
    this.post.likes += this.post.liked ? 1 : -1;
    }
  }

  onEdit() {
    this.router.navigate([`post/edit/${this.post.id}`]);
  }

  toggleComments() {
    this.post.showcmts = !this.post.showcmts;
  }
  getAvatarUrl(avatar: string) {
    return `http://localhost:8080/avatars/${avatar}`;
  }
  getMediaUrl(path: string) {
    return `http://localhost:8080/files/posts/${path}`;
  }
  Toprofile(username: string) {
    this.router.navigate([`profile/${username}`])
  }
  onReport(postId: number) {
    console.log('Reporting post', postId);
  }
  async Delete(id:number){
    let rsp = await this.psr.DeLetePost(id);
    console.log(rsp);
  }

}
