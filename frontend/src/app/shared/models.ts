import { signal, WritableSignal } from "@angular/core";

export type PostStatus = 'Active' | 'Hidden';
export interface Comment {
  id: number;
  content: string;
  timestamp: string | Date;
  author: {
    username: string;
    avatar: string;
  };
}
export interface Profile {
  followers: Author[];
  following: Author[];
  posts: WritableSignal<Post[]>;
  user: Author;
  followed: WritableSignal<boolean>;
}
export interface Epost {
  id: number;
  title: string;
  content: string;
  media?: File | null;
}
export interface ProfileResponse {
  profile: Profile;
  error: string[]
}
export interface AdminPost {
  id: number;
  title: string;
  author: Author;
  content: string;
  status: WritableSignal<boolean>;
  comments: number;
  likes: number;
}
export interface ReportPost {
  id: number;
  title: string;
  author: Author;
  content: string;
}
export interface Post {
  id: number;
  title: string;
  author: {
    name: string;
    avatar: string;
    username: string;
  };
  content: string;
  media?: {
    type: string;
    url: string;
  };
  timestamp: Date;
  likes: number;
  comments: WritableSignal<Comment[]>;
  liked: WritableSignal<boolean>;
  showcmts?: boolean;
  cmts: number;
  status: PostStatus;
}
export interface Author {
  avatar: string;
  username: string;
  status: boolean;
  id: number
}
export interface Dashboard {
  totalactiveusers: number;
  totalsuspendedusers: number;
  totalusers: number;
  totalposts: number;
  totalactiveposts: number;
  totalsuspendedposts: number;
  totalcomments: number;
  totalpendingreports: number;
  totalhandledreports: number;
  totalreports: number;
  totalpendinguserreports: number;
  totalhandleduserreports: number;
  totaluserreports: number;
  star: Author | null;
  mostlikedpost: Post | null;
  latestusers: Author[];
}
export interface Report {
  id: number;
  author: Author;
  post: ReportPost;
  reason: string;
  state: WritableSignal<boolean>;
}
export interface UserReport {
  id: number;
  reporter: Author;
  reportedUser: Author;
  reason: string;
  state: WritableSignal<boolean>;
}
export interface AdminResponse {
  reports: Report[];
  posts: AdminPost[];
  dashboard: Dashboard;
  users: Author[];
}
export interface Notification {
  sender: Author;
  content: string;
  seen: WritableSignal<boolean>;
  createdAt: string;
  id: number;
}
export function getAvatarUrl(avatar: string) {
  return `http://localhost:8080/avatars/${avatar}`;
}
export function getMediaUrl(path: string) {
  return `http://localhost:8080/files/posts/${path}`;
}