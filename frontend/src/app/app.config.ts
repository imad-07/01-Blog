import { ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';


import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { Interceptor } from './Interceptor';

import { errorInterceptor } from './Errorinterceptor';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { adminConfirmationInterceptor } from './confirmation-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(MatSnackBarModule),
    provideHttpClient(
      withFetch(),
      withInterceptors([Interceptor, errorInterceptor, adminConfirmationInterceptor]),     
    ),
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
  ]
};

