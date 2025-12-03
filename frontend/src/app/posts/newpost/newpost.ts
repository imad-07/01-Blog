import { Component, signal, WritableSignal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../auth/auth.service';
import { CommonModule } from '@angular/common';
import {Router } from '@angular/router';


@Component({
  selector: 'app-create-post',
  templateUrl: './newpost.html',
  styleUrls: ['./newpost.scss'],
  imports: [FormsModule, CommonModule]
})
export class Newpost {
  title = '';
  content = '';
  selectedFile?: File;
  previewUrl: WritableSignal<string | ArrayBuffer | null> = signal(null)
  previewType = 'img'

  constructor(private http: HttpClient, private auth: AuthService, private router :Router) { }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      if (input.files[0].type.startsWith('image/')) {
        this.previewType = 'image';
      } else if (input.files[0].type.startsWith('video/')) {
        this.previewType = 'video';
      }
        const reader = new FileReader();
  reader.onload = () => {
    this.previewUrl.set(reader.result as string);
    console.log(this.previewUrl);
    
  };
  reader.readAsDataURL(input.files[0]);
      this.selectedFile = input.files[0];
    }
  }

  onSubmit() {
    const formData = new FormData();
    formData.append('title', this.title);
    formData.append('content', this.content);
    if (this.selectedFile) {
      formData.append('media', this.selectedFile);
    }
    let token = localStorage.getItem("JWT")
    const headers = new HttpHeaders().set(
      'Authorization',
      `Bearer ${token}`
    );
    this.http.post('http://localhost:8080/post', formData, { headers, responseType: 'text' }).subscribe({
      next: () => console.log(12)
      ,
      error: err => console.error(err)
    });
  }
    onbackClick():void{
    this.router.navigate([`feed`])
  }
}
