import { HttpInterceptorFn, HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, of, EMPTY } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  const snackBar = inject(MatSnackBar);
  
  return next(req).pipe(
    catchError(error => {
      let errorMessage = 'An unexpected error occurred';
      
      if (error.error?.message) errorMessage = error.error.message;
      else if (error.status === 0) errorMessage = 'Cannot connect to the server';
      else if (error.status >= 400 && error.status < 500) errorMessage = `Client error: ${error.status}`;
      else if (error.status >= 500) errorMessage = `Server error: ${error.status}`;
      
      snackBar.open(errorMessage, 'Close', {
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['my-snackbar-error']
      });
      
      // Return EMPTY to suppress console error
      return EMPTY;
    })
  );
};