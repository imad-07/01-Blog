import { HttpInterceptorFn } from '@angular/common/http';
export const Interceptor: HttpInterceptorFn = (req, next) => {
  const authToken = localStorage.getItem('JWT');
  const authReq = authToken
    ? req.clone({ setHeaders: { Authorization: `Bearer ${authToken}` } })
    : req;
  return next(authReq);
};