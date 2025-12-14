import { HttpInterceptorFn, HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, of, EMPTY } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn) => {
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError(error => {
      let errorMessage = 'An unexpected error occurred';
        console.log(error.error);

      // Extract error message from the new backend error format
      if (error.error && typeof error.error === 'object') {
        // New format: {status: number, message: string}
        errorMessage = error.error.message;        
      } else if (error.error && typeof error.error === 'string') {
        // Fallback for plain string errors
        errorMessage = error.error;
        
      } else if (error.message) {
        // Network or other errors
        errorMessage = error.message;
      }

      snackBar.open(errorMessage, 'Close', {
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['my-snackbar-error']
      });

      return EMPTY;
    })
  );
};