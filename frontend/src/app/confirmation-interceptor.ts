import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { ConfirmationPopupComponent } from './shared/confirmation/confirmation';

export const adminConfirmationInterceptor: HttpInterceptorFn = (req, next) => {
  const dialog = inject(MatDialog);

  // Check if it's an admin endpoint and a modifying request
  const isAdminEndpoint = req.url.includes('/admin/');
  const isModifyingRequest = ['POST', 'PUT', 'DELETE', 'PATCH'].includes(req.method);

  if (isAdminEndpoint && isModifyingRequest) {
    // Return observable that waits for confirmation
    return new Observable((observer) => {
      const message = getConfirmationMessage(req);

      const dialogRef = dialog.open(ConfirmationPopupComponent, {
        data: message,
        width: '550px',
        disableClose: false
      });

      dialogRef.afterClosed().subscribe((confirmed) => {
        if (confirmed) {
          // User confirmed, proceed with request
          next(req).subscribe({
            next: (event) => observer.next(event),
            error: (err) => observer.error(err),
            complete: () => observer.complete()
          });
        }
      });
    });
  }

  // For non-admin or non-modifying requests, proceed normally
  return next(req);
};

/**
 * Generate appropriate confirmation message based on request
 */
function getConfirmationMessage(req: any): string {
  const method = req.method.toUpperCase();
  const url = req.url;

  if (url.includes('/delete') || method === 'DELETE') {
    return 'Are you sure you want to delete this? This action cannot be undone.';
  }

  if (url.includes('/suspend')) {
    return 'Are you sure you want to suspend this user?';
  }

  if (url.includes('/activate')) {
    return 'Are you sure you want to activate this user?';
  }

  if (url.includes('/ban')) {
    return 'Are you sure you want to ban this user?';
  }

  if (method === 'POST') {
    return 'Are you sure you want to create this item?';
  }

  if (method === 'PUT' || method === 'PATCH') {
    return 'Are you sure you want to update this item?';
  }

  return 'Are you sure you want to perform this action?';
}