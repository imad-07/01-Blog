import { Component, computed, signal } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class Register {
  username = signal('');
  password = signal('');
  avatarid = signal('');
  avatars: string[] = [];
  selectedAvatar = signal('');

  isUsernameValid = computed(() => this.username().length >= 4 && this.username().length <= 24);
  isPasswordValid = computed(() => this.password().length >= 4 && this.password().length <= 24);
  isFormValid = computed(() => this.isUsernameValid() && this.isPasswordValid() && this.selectedAvatar() !== '');

  constructor(private auth: AuthService, private router: Router) { }
  ngOnInit() {
    const count = 30;
    for (let i = 1; i <= count; i++) {
      this.avatars.push(`http://localhost:8080/avatars/${i}.png`);
    }
  }

  selectAvatar(avatar: string, id: number) {
    this.selectedAvatar.set(avatar);
    this.avatarid.set(id + 1 + ".png");
  }
  onSubmit() {
    if (!this.isFormValid()) {
      return;
    }
    this.auth.register(this.username(), this.password(), this.avatarid()).subscribe({
      next: (res) => {
        this.auth.saveToken(res.token);
        this.router.navigate(['/login']);
      },
      error: (err) => {
        // Handled by ErrorInterceptor
      }
    });
  }
}
