import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Service for reporting posts and users
 */
@Injectable({
    providedIn: 'root'
})
export class ReportService {
    private apiUrl = 'http://localhost:8080';

    constructor(private http: HttpClient) { }

    /**
     * Report a post
     * @param postId The ID of the post to report
     * @param reason The reason for reporting (SPAM, HARASSMENT, etc.)
     * @returns Observable<string>
     */
    reportPost(postId: number, reason: string): Observable<string> {
        const params = new HttpParams().set('reason', reason);
        return this.http.post(`${this.apiUrl}/repport/${postId}`, null, {
            params,
            responseType: 'text'
        });
    }

    /**
     * Report a user
     * @param userId The ID of the user to report
     * @param reason The reason for reporting (SPAM, HARASSMENT, etc.)
     * @returns Observable<string>
     */
    reportUser(userId: number, reason: string): Observable<string> {
        const params = new HttpParams().set('reason', reason);
        return this.http.post(`${this.apiUrl}/user-report/${userId}`, null, {
            params,
            responseType: 'text'
        });
    }
}
