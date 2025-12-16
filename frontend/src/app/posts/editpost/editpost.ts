import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Post } from '../../shared/models';

@Component({
  selector: 'app-editpost',
  templateUrl: './editpost.html',
  styleUrls: ['./editpost.scss'],
  imports: [CommonModule, FormsModule]
})
export class EditPost implements OnInit {

  postId!: number;
  title: WritableSignal<string> = signal('');
  content = '';
  type = '';
  mediaUrl?: WritableSignal<string | ArrayBuffer | null> = signal(null);
  selectedFile: WritableSignal<File | null> = signal(null);
  previewUrl: WritableSignal<string | ArrayBuffer | null> = signal(null);
  existingMediaUrl: string | null = null;
  deleteMedia = false;

  originalTitle = '';
  originalContent = '';

  constructor(private http: HttpClient, private route: ActivatedRoute, private router: Router) { }

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
          this.content = post.content;
          if (post.media && post.media.url) {
            this.mediaUrl?.set(`http://localhost:8080/files/posts/${post.media.url}`);
          } else {
            this.mediaUrl?.set(null);
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

      const reader = new FileReader();
      reader.onload = () => this.mediaUrl?.set(reader.result);
      reader.readAsDataURL(file);
      this.type = file.type.startsWith("image") ? "image" : "video";
      console.log(this.type);


    } else {
      this.selectedFile.set(null);
      this.mediaUrl?.set(null);
    }
    this.deleteMedia = false;
  }

  removeMedia() {
    this.mediaUrl?.set(null);
    this.selectedFile.set(null);
    this.deleteMedia = true;
  }

  onSubmit() {
    const token = localStorage.getItem('JWT');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    const postPayload: any = {
      id: this.postId,
      title: this.title(),
      content: this.content
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
      .subscribe(() => console.log('Post updated successfully'));
  }

  onbackClick(): void {
    this.router.navigate([`feed`])
  }
}
