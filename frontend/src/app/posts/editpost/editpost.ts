import { Component, OnInit, signal, WritableSignal, computed } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Post } from '../../shared/models';
import { SnackbarService } from '../../services/snackbar.service';

@Component({
  selector: 'app-editpost',
  templateUrl: './editpost.html',
  styleUrls: ['./editpost.scss'],
  imports: [CommonModule, FormsModule]
})
export class EditPost implements OnInit {

  postId!: number;
  title: WritableSignal<string> = signal('');
  content = signal('');
  type = '';
  mediaUrl: WritableSignal<string | ArrayBuffer | null> = signal(null);
  selectedFile: WritableSignal<File | null> = signal(null);
  previewUrl: WritableSignal<string | ArrayBuffer | null> = signal(null);
  existingMediaUrl: string | null = null;
  deleteMedia = false;

  originalTitle = '';
  originalContent = '';

  isTitleValid = computed(() => this.title().length >= 3 && this.title().length <= 30);
  isContentValid = computed(() => this.content().length >= 2 && this.content().length <= 2500);
  isFileSizeValid = computed(() => {
    const file = this.selectedFile();
    return !file || file.size <= 20 * 1024 * 1024;
  });
  isFormValid = computed(() => this.isTitleValid() && this.isContentValid() && this.isFileSizeValid());

  constructor(private http: HttpClient, private route: ActivatedRoute, private router: Router, private snackbar: SnackbarService) { }

  ngOnInit() {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    const token = localStorage.getItem('JWT');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    this.http.get<Post>(`http://localhost:8080/post/id/${this.postId}`, { headers })
      .subscribe({
        next: (post) => {
          console.log(post);
          this.type = post.media?.type || '';
          this.title.set(post.title);
          this.content.set(post.content);
          if (post.media && post.media.url) {
            this.mediaUrl.set(`http://localhost:8080/files/posts/${post.media.url}`);
          } else {
            this.mediaUrl.set(null);
          }
          this.originalTitle = post.title;
          this.originalContent = post.content;
        },
        error: (err) => console.error('Failed to load post:', err)
      });
  }
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.selectedFile.set(file);

      if (file.size <= 20 * 1024 * 1024) {
        const reader = new FileReader();
        reader.onload = () => this.mediaUrl.set(reader.result);
        reader.readAsDataURL(file);
      } else {
        this.mediaUrl.set(null);
        this.snackbar.showMessage('File size exceeds 20MB limit', true);
      }
      this.type = file.type.startsWith("image") ? "image" : "video";
    } else {
      this.selectedFile.set(null);
      this.mediaUrl.set(null);
    }
    this.deleteMedia = false;
  }

  removeMedia() {
    this.mediaUrl.set(null);
    this.selectedFile.set(null);
    this.deleteMedia = true;
  }

  onSubmit() {
    if (!this.isFormValid()) return;

    const token = localStorage.getItem('JWT');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    const postPayload: any = {
      id: this.postId,
      title: this.title(),
      content: this.content()
    };

    if (this.deleteMedia) {
      postPayload.deleteMedia = true;
    }

    const formData = new FormData();
    formData.append(
      'post',
      new Blob([JSON.stringify(postPayload)], { type: 'application/json' })
    );

    if (this.selectedFile() && !this.deleteMedia) {
      formData.append('media', this.selectedFile()!);
    }

    this.http.patch(`http://localhost:8080/post`, formData, { headers })
      .subscribe({
        next: () => {
          this.snackbar.showMessage('changed applied successfully');
          console.log('Post updated successfully');
        },
        error: (err) => {
          this.snackbar.showMessage('Failed to apply changes', true);
          console.error('Failed to update post:', err);
        }
      });
  }

  onbackClick(): void {
    this.router.navigate([`feed`])
  }
}
