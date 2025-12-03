import { Component } from '@angular/core';
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
  username = '';
  password = '';
  avatarid:string ='';
  avatars: string[] = [];
  selectedAvatar: string = '';
  constructor(private auth: AuthService, private router: Router) { }
  ngOnInit() {
    const count = 30;
    for (let i = 1; i <= count; i++) {
      this.avatars.push(`http://localhost:8080/avatars/${i}.png`);
    }
  }

  selectAvatar(avatar: string, id:number) {
    this.selectedAvatar = avatar;
    this.avatarid = id+1 +".png" 
  }
  onSubmit() {

      if (!this.selectedAvatar) {
        alert('Please select an avatar');
        return;
      }
      this.auth.register(this.username, this.password, this.avatarid).subscribe({
        next: (res) => {

          this.auth.saveToken(res.token);
          this.router.navigate(['/login']);
        },
        error: (err) => {
          alert('Register failed');
        }
      });
    }
  }
