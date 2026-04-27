import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap, delay } from 'rxjs/operators';

export interface AuthResponse {
  token: string;
  role: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthApiService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<AuthResponse> {
    // MOCK RESPONSE FOR UI TESTING
    const mockRole = credentials.email && credentials.email.toLowerCase().includes('admin') ? 'ADMIN' : 'CUSTOMER';
    return of({ token: 'mock-token-abc', role: mockRole, email: credentials.email }).pipe(
      delay(800),
      tap(res => {
          this.saveToken(res.token);
          this.saveRole(res.role);
      })
    );
  }

  register(payload: any): Observable<AuthResponse> {
    // MOCK RESPONSE FOR UI TESTING
    return of({ token: 'mock-token-abc', role: payload.role, email: payload.email }).pipe(
      delay(800)
    );
  }

  saveToken(token: string) {
    localStorage.setItem('smartsure_token', token);
  }

  saveRole(role: string) {
    localStorage.setItem('smartsure_role', role);
  }

  getToken(): string | null {
    return localStorage.getItem('smartsure_token');
  }

  getRole(): string | null {
    return localStorage.getItem('smartsure_role');
  }

  logout() {
    localStorage.removeItem('smartsure_token');
    localStorage.removeItem('smartsure_role');
  }
}
