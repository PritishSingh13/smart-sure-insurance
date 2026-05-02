import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PolicyRecord {
  id: number;
  policyName: string;
  policyType: string;
  premium: number;
  duration: number;
}

export interface ClaimRecord {
  claimNumber: string;
  policyId: number;
  claimantName: string;
  status: string;
  claimDate?: string;
  documentFileName?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CustomerApiService {
  private readonly policyUrl = 'http://localhost:8080/api/policies';
  private readonly claimsUrl = 'http://localhost:8080/api/claims';

  constructor(private http: HttpClient) {}

  getPolicies(): Observable<PolicyRecord[]> {
    return this.http.get<PolicyRecord[]>(this.policyUrl);
  }

  getMyPolicies(): Observable<PolicyRecord[]> {
    return this.http.get<PolicyRecord[]>(`${this.policyUrl}/my`);
  }

  purchasePolicy(policyId: number): Observable<string> {
    return this.http.post(`${this.policyUrl}/purchase`, { policyId }, { responseType: 'text' });
  }

  uploadClaim(file: File, policyId: number, claimantName: string): Observable<ClaimRecord> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('policyId', String(policyId));
    formData.append('claimantName', claimantName);
    return this.http.post<ClaimRecord>(`${this.claimsUrl}/upload`, formData);
  }

  initiateClaim(claimNumber: string): Observable<ClaimRecord> {
    const params = new HttpParams().set('claimNumber', claimNumber);
    return this.http.post<ClaimRecord>(`${this.claimsUrl}/initiate`, null, { params });
  }

  getMyClaims(): Observable<ClaimRecord[]> {
    return this.http.get<ClaimRecord[]>(`${this.claimsUrl}/my`);
  }

  getClaimDocument(claimNumber: string): Observable<Blob> {
    return this.http.get(`${this.claimsUrl}/${claimNumber}/document`, { responseType: 'blob' });
  }

  getClaimStatus(claimNumber: string): Observable<ClaimRecord> {
    return this.http.get<ClaimRecord>(`${this.claimsUrl}/status/${claimNumber}`);
  }
}
