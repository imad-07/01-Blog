import { Component, Input, signal, WritableSignal } from '@angular/core';
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
  
  loading = false;

  constructor(private psr: PostService) {    
  }

  getAvatarUrl(avatar: string): string {
    return `http://localhost:8080/avatars/${avatar}`;
  }

  async onSubmit(input: HTMLInputElement) {
    const content = input.value.trim();
    if (!content) return;

    await this.psr.addComment(this.postId, content);
    input.value = '';
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
