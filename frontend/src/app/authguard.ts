import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from './auth/auth.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {
    constructor(private router: Router, private auth: AuthService) { }

   async canActivate(): Promise<boolean> {
        if (typeof window === 'undefined') {
      this.router.navigate(['/login']);
      
      return false;
    }
        let x = await this.auth.authenticate()
        if (!x){
            this.router.navigate(['/login']);
        }
        return x
    }
}
