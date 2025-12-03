import { ChangeDetectorRef, Component } from '@angular/core';
import { Author, getAvatarUrl } from '../models';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';
import { NotificationComponent } from '../../notification/notification'
@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
  imports: [NotificationComponent]
})
export class Navbar {
  author: Author = {
    id: 1,
    username: "",
    avatar: "",
    status: false
  };

  constructor(private psr: AuthService, private cdr: ChangeDetectorRef, private router: Router) { }


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
  profile() {
    this.author.username == 'admin' ? this.router.navigate(["admin"]) : this.router.navigate(["profile"])
  }
}
