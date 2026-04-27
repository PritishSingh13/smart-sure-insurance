import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthApiService } from './features/auth/services/auth-api.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthApiService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    
    if (this.authService.getToken()) {
        const expectedRole = route.data['role'];
        if (expectedRole && this.authService.getRole() !== expectedRole) {
            // Not authorized for this specific dashboard
            this.router.navigate(['/']); 
            return false;
        }
        return true;
    }
    
    // Fallback: Not logged in
    this.router.navigate(['/']);
    return false;
  }
}
