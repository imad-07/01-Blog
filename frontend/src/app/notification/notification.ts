import { ChangeDetectorRef, Component, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Notification } from "../shared/models";
import { PostService } from '../posts/authpost';


@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification.html',
  styleUrls: ['./notification.scss']
})
export class NotificationComponent implements OnInit {
  isOpen = false;
  loadingnotif = false;
  lastid = 0;
  notifications: WritableSignal<Notification[]> = signal([]);
  constructor(private psr: PostService, private cdr: ChangeDetectorRef) {
  }
  async ngOnInit() {
    this.loadingnotif = true;
    this.notifications.set(await this.psr.getNotifs(0))
    this.loadingnotif = false;
    this.lastid = this.notifications().length > 0 ? this.notifications()[this.notifications().length - 1].id : 1
    this.cdr.detectChanges();
  }
  getAvatarUrl(avatar: string): string {
    return `http://localhost:8080/avatars/${avatar}`;
  }
  get unseenCount(): number {
    return this.notifications().filter(n => !n.seen()).length;
  }

  toggleNotifications(): void {
    this.isOpen = !this.isOpen;
  }

  closeNotifications(): void {
    this.isOpen = false;
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);
    if (minutes < 1) return 'just now';
    if (minutes < 60) return `${minutes}m ago`;
    if (hours < 24) return `${hours}h ago`;
    if (days < 7) return `${days}d ago`;
    return date.toLocaleDateString();
  }

  async ToggleSeen(notif: Notification) {
    const reslt = await this.psr.UpdateSeen(notif.id);
    if (reslt) {
      notif.seen.set(!notif.seen());
    }
    console.log(reslt);
  }

  async deleteNotification(notif: Notification) {
    const reslt = await this.psr.DeleteNotif(notif.id);
    if (reslt) {
      this.notifications.set(this.notifications().filter(n => n.id !== notif.id));
    }
    console.log(reslt);
  }

  async markAllAsSeen() {
    const reslt = await this.psr.SeenAllNotif();
    if (reslt) {
      this.notifications().forEach(n => n.seen.set(true));
    }
  }

  async clearAll() {
    const reslt = await this.psr.DeleteAllNotif()
    if (reslt) {
      this.notifications.set([]);
    }
  }
  async Notifinfinitescroll(ev: Event) {
    if (this.loadingnotif) return;
    const e = ev.target as HTMLElement;
    if (e.scrollHeight - e.scrollTop <= e.clientHeight + 20) {
      this.loadingnotif = true;
      this.lastid = this.notifications().length > 0 ? this.notifications()[this.notifications().length - 1].id : 1
      const notifs = await this.psr.getNotifs(this.lastid);
      this.notifications.update(n => [...n, ...notifs]);
      this.lastid = notifs.length > 0 ? notifs[notifs.length - 1].id : 1;
      this.loadingnotif = false;
    }
  }
}