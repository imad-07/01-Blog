import { Component, signal, WritableSignal, computed } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../auth/auth.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SnackbarService } from '../../services/snackbar.service';


@Component({
  selector: 'app-create-post',
  templateUrl: './newpost.html',
  styleUrls: ['./newpost.scss'],
  imports: [FormsModule, CommonModule]
})
export class Newpost {
  title = signal('');
  content = signal('');
  selectedFile = signal<File | null>(null);
  previewUrl: WritableSignal<string | ArrayBuffer | null> = signal(null)
  previewType = signal<'image' | 'video' | 'none'>('none');

  isTitleValid = computed(() => this.title().length >= 3 && this.title().length <= 30);
  isContentValid = computed(() => this.content().length >= 2 && this.content().length <= 2500);
  isFileSizeValid = computed(() => {
    const file = this.selectedFile();
    return !file || file.size <= 20 * 1024 * 1024;
  });
  isFormValid = computed(() => this.isTitleValid() && this.isContentValid() && this.isFileSizeValid());

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private router: Router,
    private snackbar: SnackbarService
  ) { }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      const file = input.files[0];
      this.selectedFile.set(file);

      if (file.type.startsWith('image/')) {
        this.previewType.set('image');
      } else if (file.type.startsWith('video/')) {
        this.previewType.set('video');
      }

      if (file.size <= 20 * 1024 * 1024) {
        const reader = new FileReader();
        reader.onload = () => {
          this.previewUrl.set(reader.result as string);
        };
        reader.readAsDataURL(file);
      } else {
        this.previewUrl.set(null);
        this.snackbar.showMessage('File size exceeds 20MB limit', true);
      }
    }
  }

  async onSubmit() {
    if (!this.isFormValid()) return;

    const formData = new FormData();
    formData.append('title', this.title());
    formData.append('content', this.content());
    const file = this.selectedFile();
    if (file) {
      formData.append('media', file);
    }
    let token = localStorage.getItem("JWT")
    const headers = new HttpHeaders().set(
      'Authorization',
      `Bearer ${token}`
    );
    this.http.post('http://localhost:8080/post', formData, { headers, responseType: 'text' }).subscribe({
      next: async () => {
        this.snackbar.showMessage('Post created successfully');
        this.router.navigate(['profile/' + (await this.auth.whoami()).username]);
      },
      error: err => {
        console.error(err);
        this.snackbar.showMessage('Failed to create post', true);
      }
    });
  }
  onbackClick(): void {
    this.router.navigate([`feed`])
  }
}
