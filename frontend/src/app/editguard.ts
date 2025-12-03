import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from "@angular/router";
import { AuthService } from "./auth/auth.service";
import { PostService } from "./posts/authpost";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Post } from "./shared/models";
import { firstValueFrom } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class Editguard implements CanActivate {
    constructor(private router: Router, private auth: AuthService, private http: HttpClient) { }
    async canActivate(    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Promise<boolean> {
        let me = await this.auth.whoami()
        let id = state.url.slice(state.url.lastIndexOf("/") + 1)
        const token = localStorage.getItem('JWT');
        const headers = new HttpHeaders().set(
            'Authorization',
            `Bearer ${token}`
        );
        try {
            let post = await firstValueFrom(this.http.get<Post>(`http://localhost:8080/post/id/${id}`, { headers }));    
            console.log(me.username);
                    
            if (me.username === post.author.username || me.username == 'admin') {
                
                return true
            }
            return false
        } catch (err) {
            console.error('Post fetch failed', err);
            this.router.navigate(['/feed']);
            return false;
        }
    }
}