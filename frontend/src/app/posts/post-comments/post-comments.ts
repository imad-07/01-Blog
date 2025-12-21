import { Component, Input, Output, EventEmitter, signal, WritableSignal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PostService } from '../authpost';
import { Comment } from '../../shared/models';
@Component({
  selector: 'app-post-comments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './post-comments.html',
  styleUrls: ['./post-comments.scss']
})
export class PostCommentsComponent {
  @Input() postId!: number;
  @Input() comments: WritableSignal<Comment[]> = signal([]);
  @Input() backendUrl!: string;
  @Input() currentuser: string = "";
  @Output() commentAdded = new EventEmitter<void>();

  newCommentText = signal('');
  loading = false;

  isCommentValid = computed(() => {
    const text = this.newCommentText().trim();
    return text.length >= 2 && text.length <= 2500;
  });

  constructor(private psr: PostService) {
  }

  getAvatarUrl(avatar: string): string {
    return `http://localhost:8080/avatars/${avatar}`;
  }

  async onSubmit() {
    const content = this.newCommentText().trim();
    if (!this.isCommentValid()) return;

    const newComment = await this.psr.addComment(this.postId, content);
    if (newComment) {
      this.comments.update(c => [newComment, ...c]);
      this.commentAdded.emit();
      this.newCommentText.set('');
    }
  }

  async deleteComment(commentId: number) {
    await this.psr.deletecmt(commentId);
    this.comments.update(c => c.filter(cm => cm.id !== commentId));
  }

  async onScroll(ev: Event) {
    if (this.loading) return;
    const e = ev.target as HTMLElement;
    if (e.scrollHeight - e.scrollTop <= e.clientHeight + 100) {
      this.loading = true;
      const lastId = this.comments()[this.comments().length - 1]?.id || 0;
      const newComments = await this.psr.getComment(this.postId, lastId);
      this.comments.update(c => [...c, ...newComments]);
      this.loading = false;
    }
  }
}
