import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthApiService, UserProfile } from '../../features/auth/services/auth-api.service';

export interface PolicyRecord {
  id: number;
  policyName: string;
  policyType: string;
  premium: number;
  duration: number;
}

export interface ClaimRecord {
  id: number;
  claimNumber: string;
  policyId: number;
  claimantName: string;
  status: string;
  claimDate?: string;
  createdBy?: string;
  documentPath?: string;
}

export interface ReportSummary {
  TOTAL_CLAIMS: number;
  APPROVED: number;
  REJECTED: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminApiService {
  private readonly adminUrl = 'http://localhost:8080/api/admin';

  constructor(
    private http: HttpClient,
    private authApiService: AuthApiService
  ) {}

  getAllClaims(): Observable<ClaimRecord[]> {
    return this.http.get<ClaimRecord[]>(`${this.adminUrl}/claims`);
  }

  reviewClaim(claimId: number, status: string): Observable<string> {
    const params = new HttpParams().set('status', status);
    return this.http.put(`${this.adminUrl}/claims/${claimId}/review`, null, { params, responseType: 'text' });
  }

  getReports(): Observable<ReportSummary> {
    return this.http.get<ReportSummary>(`${this.adminUrl}/reports`);
  }

  getAllUsers(): Observable<UserProfile[]> {
    return this.authApiService.getAllUsers();
  }

  getPolicies(): Observable<PolicyRecord[]> {
    return this.http.get<PolicyRecord[]>(`${this.adminUrl}/policies`);
  }

  createPolicy(payload: Omit<PolicyRecord, 'id'>): Observable<PolicyRecord> {
    return this.http.post<PolicyRecord>(`${this.adminUrl}/policies`, payload);
  }

  deletePolicy(id: number): Observable<string> {
    return this.http.delete(`${this.adminUrl}/policies/${id}`, { responseType: 'text' });
  }

  getClaimDocument(claimId: number): Observable<Blob> {
    return this.http.get(`${this.adminUrl}/claims/${claimId}/document`, { responseType: 'blob' });
  }

  getClaimDocumentPreviewUrl(claimId: number): string {
    const token = this.authApiService.getToken();
    const params = token ? `?access_token=${encodeURIComponent(token)}` : '';
    return `${this.adminUrl}/claims/${claimId}/document${params}`;
  }
}
