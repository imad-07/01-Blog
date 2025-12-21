import { ChangeDetectorRef, Component } from '@angular/core';
import { Author, getAvatarUrl } from '../models';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NotificationComponent } from '../../notification/notification'
@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
  imports: [NotificationComponent, CommonModule]
})
export class Navbar {
  author: Author = {
    id: 1,
    username: "",
    avatar: "",
    status: false,
    role: "USER"
  };

  constructor(private psr: AuthService, private cdr: ChangeDetectorRef, public router: Router) { }


  async ngOnInit() {
    this.author = await this.psr.whoami() || { username: "", avatar: "" };
    this.author.avatar = getAvatarUrl(this.author.avatar)
    this.cdr.detectChanges();
  }

  logout() {
    localStorage.removeItem("JWT");
    location.reload();
  }
  newpost() {
    this.router.navigate(["newpost"])
  }
  feed() {
    this.router.navigate(["feed"])
  }
  explore() {
    this.router.navigate(["users"])
  }
  profile() {
    this.author.role === 'ADMIN' ? this.router.navigate(["admin"]) : this.router.navigate(["/profile/" + this.author.username])
  }
}
