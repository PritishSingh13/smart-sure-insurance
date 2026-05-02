import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface AuthResponse {
  token: string;
  role: string;
  email: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  name: string;
  email: string;
  password: string;
  phone: string;
  address: string;
  role: string;
}

export interface UserProfile {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  role: string;
  profileImage?: string | null;
}

export interface UpdateProfilePayload {
  name: string;
  email: string;
  phone: string;
  address: string;
  profileImage?: string | null;
}

export interface ProfileUpdateResponse extends AuthResponse {
  user: UserProfile;
}

@Injectable({
  providedIn: 'root'
})
export class AuthApiService {
  private readonly apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(credentials: LoginPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => this.saveSession(response))
    );
  }

  register(payload: RegisterPayload): Observable<string> {
    return this.http.post(`${this.apiUrl}/register`, payload, {
      responseType: 'text'
    });
  }

  getCurrentUser(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/me`);
  }

  updateCurrentUser(payload: UpdateProfilePayload): Observable<ProfileUpdateResponse> {
    return this.http.put<ProfileUpdateResponse>(`${this.apiUrl}/me`, payload).pipe(
      tap((response) => this.saveSession(response))
    );
  }

  getAllUsers(): Observable<UserProfile[]> {
    return this.http.get<UserProfile[]>(`${this.apiUrl}/users`);
  }

  saveSession(response: AuthResponse) {
    this.saveToken(response.token);
    this.saveRole(response.role);
    this.saveEmail(response.email);
  }

  saveToken(token: string) {
    localStorage.setItem('smartsure_token', token);
  }

  saveRole(role: string) {
    localStorage.setItem('smartsure_role', role);
  }

  saveEmail(email: string) {
    localStorage.setItem('smartsure_email', email);
  }

  getToken(): string | null {
    return localStorage.getItem('smartsure_token');
  }

  getRole(): string | null {
    return localStorage.getItem('smartsure_role');
  }

  getEmail(): string | null {
    return localStorage.getItem('smartsure_email');
  }

  logout() {
    localStorage.removeItem('smartsure_token');
    localStorage.removeItem('smartsure_role');
    localStorage.removeItem('smartsure_email');
  }
}
