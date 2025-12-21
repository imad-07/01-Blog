import { Component, computed, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {

  username = signal('');
  password = signal('');

  isUsernameValid = computed(() => this.username().length >= 4 && this.username().length <= 24);
  isPasswordValid = computed(() => this.password().length >= 4 && this.password().length <= 24);
  isFormValid = computed(() => this.isUsernameValid() && this.isPasswordValid());

  constructor(private auth: AuthService, private router: Router) { }

  onSubmit() {
    if (!this.isFormValid()) return;

    this.auth.login(this.username(), this.password()).subscribe({
      next: (res) => {
        localStorage.setItem("JWT", res.jwt)
        this.router.navigate(['/']);
      },
      error: (err) => {
        // Handled by ErrorInterceptor
      }
    });
  }
}
