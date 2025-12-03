import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom, Observable } from 'rxjs';
import { Author } from '../shared/models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/auth';

  constructor(private http: HttpClient) { }

  register(username: string, password: string, avatar: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, { username, password, avatar });
  }


  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { username, password });
  }

  saveToken(token: string) {
    localStorage.setItem('JWT', token);
  }

  getToken(): string | null {
    return localStorage.getItem('JWT');
  }
  async authenticate() {
    try {
      const response = await firstValueFrom(
        this.http.get('http://localhost:8080/guard', { observe: 'response' })
      );
      return response.status === 200;
    } catch (err) {
      console.log('Authentication failed:', err);
      return false;
    }
  }
  logout() {
    localStorage.removeItem('JWT');
  }
    async whoami(): Promise<Author> {
    const token = localStorage.getItem('JWT');
    const headers = new HttpHeaders().set(
      'Authorization',
      `Bearer ${token}`
    );
    
    const name = await firstValueFrom(
      this.http.get<Author>("http://localhost:8080/whoami",{headers})
    )
    return name;
  }
}
