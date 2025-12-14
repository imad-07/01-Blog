import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { errorInterceptor } from './Errorinterceptor';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { provideZonelessChangeDetection } from '@angular/core';

describe('errorInterceptor', () => {
    let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
    const interceptor: HttpInterceptorFn = (req, next) =>
        TestBed.runInInjectionContext(() => errorInterceptor(req, next));

    beforeEach(() => {
        snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
        TestBed.configureTestingModule({
            providers: [
                { provide: MatSnackBar, useValue: snackBarSpy },
                provideZonelessChangeDetection()
            ]
        });
    });

    it('should be created', () => {
        expect(interceptor).toBeTruthy();
    });

    it('should handle string error (ResponseEntity<String>)', (done) => {
        const errorResponse = new HttpErrorResponse({
            error: 'Backend String Error',
            status: 400
        });
        const nextMock: HttpHandlerFn = req => throwError(() => errorResponse);
        const reqMock = new HttpRequest('GET', '/test');

        interceptor(reqMock, nextMock).subscribe({
            error: () => {
                expect(snackBarSpy.open).toHaveBeenCalledWith(
                    'Backend String Error',
                    'Close',
                    jasmine.objectContaining({ panelClass: ['my-snackbar-error'] })
                );
                done();
            },
            complete: () => done()
        });
    });

    it('should handle JSON error object with message property', (done) => {
        const errorResponse = new HttpErrorResponse({
            error: { message: 'Backend JSON Message' },
            status: 400
        });
        const nextMock: HttpHandlerFn = req => throwError(() => errorResponse);
        const reqMock = new HttpRequest('GET', '/test');

        interceptor(reqMock, nextMock).subscribe({
            error: () => {
                expect(snackBarSpy.open).toHaveBeenCalledWith(
                    'Backend JSON Message',
                    'Close',
                    jasmine.objectContaining({ panelClass: ['my-snackbar-error'] })
                );
                done();
            },
            complete: () => done()
        });
    });

    it('should handle JSON error object with error property (fallback)', (done) => {
        const errorResponse = new HttpErrorResponse({
            error: { error: 'Backend JSON Error Prop' },
            status: 400
        });
        const nextMock: HttpHandlerFn = req => throwError(() => errorResponse);
        const reqMock = new HttpRequest('GET', '/test');

        interceptor(reqMock, nextMock).subscribe({
            error: () => {
                expect(snackBarSpy.open).toHaveBeenCalledWith(
                    'Backend JSON Error Prop',
                    'Close',
                    jasmine.objectContaining({ panelClass: ['my-snackbar-error'] })
                );
                done();
            },
            complete: () => done()
        });
    });

    it('should handle network error (ProgressEvent)', (done) => {
        const errorResponse = new HttpErrorResponse({
            error: new ProgressEvent('error'),
            status: 0
        });
        const nextMock: HttpHandlerFn = req => throwError(() => errorResponse);
        const reqMock = new HttpRequest('GET', '/test');

        interceptor(reqMock, nextMock).subscribe({
            error: () => {
                expect(snackBarSpy.open).toHaveBeenCalledWith(
                    'Network Error - Please check your connection',
                    'Close',
                    jasmine.objectContaining({ panelClass: ['my-snackbar-error'] })
                );
                done();
            },
            complete: () => done()
        });
    });

    it('should handle status 0 without ProgressEvent', (done) => {
        const errorResponse = new HttpErrorResponse({
            status: 0
        });
        const nextMock: HttpHandlerFn = req => throwError(() => errorResponse);
        const reqMock = new HttpRequest('GET', '/test');

        interceptor(reqMock, nextMock).subscribe({
            error: () => {
                expect(snackBarSpy.open).toHaveBeenCalledWith(
                    'Network Error - Unable to reach server',
                    'Close',
                    jasmine.objectContaining({ panelClass: ['my-snackbar-error'] })
                );
                done();
            },
            complete: () => done()
        });
    });
});
