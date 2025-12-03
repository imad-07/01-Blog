import { Routes } from '@angular/router';
import { AuthGuard } from './authguard';
// Auth
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';

// Posts
import { PostListComponent } from './posts/post-list/post-list';

// Profile

import { Newpost } from './posts/newpost/newpost';
import { EditPost } from './posts/editpost/editpost';
import { Home } from './shared/home/home';
import { Editguard } from './editguard';
import { ProfileComponent } from './profile/profile';
import { Admin } from './admin/admin';

export const routes: Routes = [
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    { path: 'admin', component: Admin, canActivate: [AuthGuard] },
    {
        path: '', component: Home, children: [
            { path: 'newpost', component: Newpost, canActivate: [AuthGuard] },
            { path: 'feed', component: PostListComponent, canActivate: [AuthGuard] },
            { path: 'post/edit/:id', component: EditPost, canActivate: [AuthGuard, Editguard] },
            { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
            { path: '**', redirectTo: 'feed' }
        ]
    }

];

