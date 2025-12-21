import { Injectable, isSignal, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import {
  Post, Comment, ProfileResponse, AdminResponse, AdminPost, Report,
  Notification, Author, ReportPost, Dashboard, Profile, UserReport
} from '../shared/models';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = 'http://localhost:8080/';

  constructor(private http: HttpClient) { }
  private async safe<T>(promise: Promise<T>, fallback: T): Promise<T> {
    try {
      return await promise;
    } catch {
      return fallback;
    }
  }

  private EMPTY_AUTHOR: Author = {
    id: 0,
    username: 'unknown',
    avatar: '',
    status: false,
    role: 'USER'
  };

  private EMPTY_POST: Post = {
    id: 0,
    title: '',
    author: {
      name: 'unknown',
      avatar: '',
      username: 'unknown'
    },
    content: '',
    media: undefined,
    timestamp: new Date(),
    likes: 0,
    comments: signal<Comment[]>([]),
    liked: signal(false),
    showcmts: false,
    cmts: 0,
    status: 'Active'
  };
  private EMPTY_DASHBOARD: Dashboard = {
    totalactiveusers: 0,
    totalsuspendedusers: 0,
    totalusers: 0,
    totalposts: 0,
    totalactiveposts: 0,
    totalsuspendedposts: 0,
    totalcomments: 0,
    totalpendingreports: 0,
    totalhandledreports: 0,
    totalreports: 0,
    totalpendinguserreports: 0,
    totalhandleduserreports: 0,
    totaluserreports: 0,
    star: { ...this.EMPTY_AUTHOR },
    mostlikedpost: { ...this.EMPTY_POST },
    latestusers: []
  };

  private EMPTY_PROFILE: Profile = {
    followers: [],
    following: [],
    posts: signal<Post[]>([]),
    user: this.EMPTY_AUTHOR,
    followed: signal(false)
  };

  private EMPTY_PROFILE_RESPONSE: ProfileResponse = {
    profile: this.EMPTY_PROFILE,
    error: []
  };

  private EMPTY_ADMIN_RESPONSE: AdminResponse = {
    reports: [],
    posts: [],
    dashboard: this.EMPTY_DASHBOARD,
    users: []
  };

  private EMPTY_NOTIFICATION: Notification = {
    id: 0,
    sender: this.EMPTY_AUTHOR,
    content: '',
    seen: signal(false),
    createdAt: ''
  };
  async getposts(start: number): Promise<Post[]> {
    return this.safe(
      (async () => {
        const ps = await firstValueFrom(this.http.get<Post[]>(`${this.apiUrl}post/${start}`));
        return (ps ?? []).map(e => {
          if (!isSignal(e.comments)) e.comments = signal(e.comments ?? []);
          e.showcmts = false;
          if (!isSignal(e.liked)) e.liked = signal(e.liked ?? false);
          return e;
        });
      })(),
      []
    );
  }

  async getmyposts(start: number): Promise<Post[]> {
    return this.safe(
      (async () => {
        const ps = await firstValueFrom(this.http.get<Post[]>(`${this.apiUrl}post/mine/${start}`));
        return (ps ?? []).map(e => {
          if (!isSignal(e.comments)) e.comments = signal(e.comments ?? []);
          e.showcmts = false;
          if (!isSignal(e.liked)) e.liked = signal(e.liked ?? false);
          return e;
        });
      })(),
      []
    );
  }

  async DeLetePost(id: number): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}post/${id}`)),
      false
    );
  }

  async like(postid: Number): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.post(`${this.apiUrl}like/${postid}`, {}, { responseType: 'json' }))
        .then(() => true),
      false
    );
  }

  async addComment(postid: number, content: string): Promise<Comment | null> {
    return this.safe(
      firstValueFrom(this.http.post<Comment>(`${this.apiUrl}comment/${postid}`, content))
        .then(res => res),
      null
    );
  }

  async getComment(post: number, start: number): Promise<Comment[]> {
    return this.safe(
      firstValueFrom(this.http.get<Comment[]>(`${this.apiUrl}comment/${post}/${start}`)),
      []
    );
  }

  async deletecmt(commentid: number): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.delete(`${this.apiUrl}comment/${commentid}`, { responseType: 'text' }))
        .then(res => res === 'Done'),
      false
    );
  }

  async fetchProfile(username: String): Promise<ProfileResponse> {
    return this.safe(
      (async () => {
        const response = await firstValueFrom(this.http.get<ProfileResponse>(`${this.apiUrl}profile/${username}`));

        if (response && response.profile) {
          if (!isSignal(response.profile.posts)) response.profile.posts = signal(response.profile.posts ?? []);
          if (!isSignal(response.profile.followed)) response.profile.followed = signal(response.profile.followed ?? false);
          response.profile.posts().forEach(p => {
            if (!isSignal(p.comments)) p.comments = signal(p.comments ?? []);
            if (!isSignal(p.liked)) p.liked = signal(p.liked ?? false);
          });
        }

        return response ?? this.EMPTY_PROFILE_RESPONSE;
      })(),
      this.EMPTY_PROFILE_RESPONSE
    );
  }

  async follow(username: string): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.post<boolean>(`${this.apiUrl}follow/${username}`, {})),
      false
    );
  }

  async report(postId: number, reason: String): Promise<String> {
    return this.safe(
      firstValueFrom(this.http.post<String>(`${this.apiUrl}repport/${postId}?reason=${encodeURIComponent(String(reason))}`, {})),
      'Error'
    );
  }

  async GetAdmin(): Promise<AdminResponse> {
    return this.safe(
      (async () => {
        const ps = await firstValueFrom(this.http.get<AdminResponse>(`${this.apiUrl}admin`));
        const safePs = ps ?? this.EMPTY_ADMIN_RESPONSE;

        (safePs.posts ?? []).forEach(p => {
          if (!isSignal(p.status)) p.status = signal(p.status ?? true);
        });
        (safePs.reports ?? []).forEach(r => {
          if (!isSignal(r.state)) r.state = signal(r.state ?? true);
        });
        safePs.dashboard = safePs.dashboard ?? this.EMPTY_DASHBOARD;

        return safePs;
      })(),
      this.EMPTY_ADMIN_RESPONSE
    );
  }

  async DeletePost(id: number): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}admin/${id}`)),
      false
    );
  }

  async UpdatePostStatus(id: number): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.patch<boolean>(`${this.apiUrl}admin/status/${id}`, {})),
      false
    );
  }

  async UpdateReportStatus(id: number): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.patch<boolean>(`${this.apiUrl}admin/report/${id}`, {})),
      false
    );
  }

  async UpdateUserStatus(id: number): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.patch<boolean>(`${this.apiUrl}admin/userstatus/${id}`, {})),
      false
    );
  }

  async UpdateUserReportStatus(id: number): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.patch<boolean>(`${this.apiUrl}admin/user-report/${id}`, {})),
      false
    );
  }

  async getAdminposts(start: number): Promise<AdminPost[]> {
    return this.safe(
      (async () => {
        const ps = await firstValueFrom(this.http.get<AdminPost[]>(`${this.apiUrl}admin/posts/${start}`));
        const arr = ps ?? [];
        arr.forEach(p => {
          if (!isSignal(p.status)) p.status = signal(p.status ?? true);
        });
        return arr;
      })(),
      []
    );
  }

  async getAdminreports(start: number): Promise<Report[]> {
    return this.safe(
      (async () => {
        const ps = await firstValueFrom(this.http.get<Report[]>(`${this.apiUrl}admin/reports/${start}`));
        const arr = ps ?? [];
        arr.forEach(r => {
          if (!isSignal(r.state)) r.state = signal(r.state ?? true);
        });
        return arr;
      })(),
      []
    );
  }

  async getAdminusers(start: number): Promise<Author[]> {
    return this.safe(
      firstValueFrom(this.http.get<Author[]>(`${this.apiUrl}admin/user/${start}`)),
      []
    );
  }

  async getAdminUserReports(start: number): Promise<UserReport[]> {
    return this.safe(
      (async () => {
        const reports = await firstValueFrom(this.http.get<UserReport[]>(`${this.apiUrl}admin/user-reports/${start}`));
        const arr = reports ?? [];
        arr.forEach(r => {
          if (!isSignal(r.state)) r.state = signal(r.state ?? false);
        });
        return arr;
      })(),
      []
    );
  }

  async getNotifs(start: number): Promise<Notification[]> {
    return this.safe(
      (async () => {
        const ns = await firstValueFrom(this.http.get<Notification[]>(`${this.apiUrl}notif/${start}`));
        const arr = ns ?? [];
        arr.forEach(n => {
          if (!isSignal(n.seen)) n.seen = signal(n.seen ?? false);
        });
        return arr;
      })(),
      []
    );
  }

  async UpdateSeen(id: number): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.patch<boolean>(`${this.apiUrl}notif/seen/${id}`, {})),
      false
    );
  }

  async DeleteNotif(id: number): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.patch<boolean>(`${this.apiUrl}notif/delete/${id}`, {})),
      false
    );
  }

  async DeleteAllNotif(): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.patch<boolean>(`${this.apiUrl}notif/delete-all`, {})),
      false
    );
  }

  async SeenAllNotif(): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.patch<boolean>(`${this.apiUrl}notif/seen-all`, {})),
      false
    );
  }
  async getUsersList(id: number): Promise<Author[]> {
    return this.safe(
      firstValueFrom(this.http.get<Author[]>(`${this.apiUrl}user/${id}`)),
      [])
  }
  async Deleteuser(id: number): Promise<boolean> {
    return this.safe(
      firstValueFrom(this.http.delete<boolean>(`${this.apiUrl}admin/rm-user/${id}`)),
      false
    );
  }
}
