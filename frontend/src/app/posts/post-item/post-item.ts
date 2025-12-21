import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PostService } from '../authpost';
import { Post } from '../../shared/models';
import { PostCommentsComponent } from '../post-comments/post-comments';
import { SnackbarService } from '../../services/snackbar.service';

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
  @Output() onDelete = new EventEmitter<number>();

  constructor(
    private psr: PostService,
    private router: Router,
    private snackbar: SnackbarService
  ) { }
  // post-item.component.ts
  reportReasons = ['SPAM', 'HARASSMENT', 'INAPPROPRIATE', 'COPYRIGHT', 'MISINFORMATION', 'OTHER'];

  showReportPopup = signal(false);
  selectedPostId = signal<number | null>(null);

  openReportPopup(postId: number) {
    this.selectedPostId.set(postId);
    this.showReportPopup.set(true);
  }

  closeReportPopup() {
    this.showReportPopup.set(false);
    this.selectedPostId.set(null);
  }

  async submitReport(reason: string) {
    let rsp = await this.psr.report(this.post.id, reason);
    this.closeReportPopup();
    if (rsp !== 'Error') {
      this.snackbar.showMessage('Report submitted successfully');
    } else {
      this.snackbar.showMessage('Failed to submit report', true);
    }

  }
  async onLike() {
    let rsp = await this.psr.like(this.post.id);
    if (rsp) {
      this.post.liked.set(!this.post.liked());
      this.post.likes += this.post.liked() ? 1 : -1;
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
  async Delete(id: number) {
    let rsp = await this.psr.DeLetePost(id);
    if (rsp) {
      this.snackbar.showMessage('Post deleted successfully');
      this.onDelete.emit(id);
    } else {
      this.snackbar.showMessage('Failed to delete post', true);
    }
  }

  onCommentAdded() {
    this.post.cmts++;
  }

}
