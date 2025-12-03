import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [RouterLink, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  
username = '';
  password = '';

  constructor(private auth: AuthService, private router: Router) {
  }

  onSubmit() {
    this.auth.login(this.username, this.password).subscribe({
      next: (res) => {
        localStorage.setItem("JWT", res.jwt)
        this.router.navigate(['/']);
      },
      error: (err) => {
        alert('Login failed');
      }
    });
  }
}
