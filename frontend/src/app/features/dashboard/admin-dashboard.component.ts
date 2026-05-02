import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { catchError, finalize, timeout } from 'rxjs/operators';
import { AdminApiService, ClaimRecord, PolicyRecord, ReportSummary } from '../../core/services/admin-api.service';
import { AuthApiService, UserProfile } from '../auth/services/auth-api.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  standalone: false
})
export class AdminDashboardComponent implements OnInit, OnDestroy {
  activeTab: 'claims' | 'policies' | 'reports' = 'claims';

  claims: ClaimRecord[] = [];
  policies: PolicyRecord[] = [];
  users: UserProfile[] = [];
  reports: ReportSummary = { TOTAL_CLAIMS: 0, APPROVED: 0, REJECTED: 0 };

  searchQuery = '';
  expandedRow: number | null = null;
  showDocModal = false;
  selectedClaim: ClaimRecord | null = null;
  documentPreviewUrl: SafeResourceUrl | null = null;
  rawDocumentUrl: string | null = null;
  isDocumentLoading = false;
  documentLoadingClaimId: number | null = null;
  isInitialLoading = true;
  reviewingClaimId: number | null = null;

  showPolicyForm = false;
  isPolicySaving = false;
  showDeleteConfirm = false;
  pendingDeletePolicy: PolicyRecord | null = null;
  newPolicy: Omit<PolicyRecord, 'id'> = { policyName: '', policyType: 'LIFE', premium: 0, duration: 1 };

  toast = { show: false, message: '', type: 'success' as 'success' | 'error' };

  constructor(
    private authService: AuthApiService,
    private adminApiService: AdminApiService,
    private router: Router,
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadDashboard();
  }

  ngOnDestroy() {
    this.revokeDocumentUrl();
  }

  switchTab(tab: 'claims' | 'policies' | 'reports') {
    this.activeTab = tab;
    this.searchQuery = '';
  }

  pulseBrand() {
    this.showToast('SmartSure command center is active.', 'success');
  }

  loadDashboard() {
    this.isInitialLoading = true;

    this.adminApiService.getAllClaims().pipe(
      timeout(5000),
      catchError(() => {
        this.showToast('Unable to load claims.', 'error');
        return of([]);
      }),
      finalize(() => {
        this.isInitialLoading = false;
        this.cdr.detectChanges();
      })
    ).subscribe((claims) => {
      this.claims = claims;
    });

    this.loadSecondaryData();
  }

  private loadSecondaryData() {
    this.adminApiService.getPolicies().pipe(
      timeout(6000),
      catchError(() => {
        this.showToast('Unable to load policy catalog.', 'error');
        return of([]);
      })
    ).subscribe((policies) => {
      this.policies = policies;
    });

    this.adminApiService.getReports().pipe(
      timeout(6000),
      catchError(() => {
        this.showToast('Unable to load system reports.', 'error');
        return of({ TOTAL_CLAIMS: 0, APPROVED: 0, REJECTED: 0 });
      })
    ).subscribe((reports) => {
      this.reports = reports;
    });

    this.adminApiService.getAllUsers().pipe(
      timeout(6000),
      catchError(() => {
        this.showToast('Unable to load users.', 'error');
        return of([]);
      })
    ).subscribe((users) => {
      this.users = users;
    });
  }

  refreshClaims() {
    this.adminApiService.getAllClaims().subscribe({
      next: (claims) => {
        this.claims = claims;
      },
      error: () => this.showToast('Unable to load claims.', 'error')
    });
  }

  refreshPolicies() {
    this.adminApiService.getPolicies().subscribe({
      next: (policies) => {
        this.policies = policies;
      },
      error: () => this.showToast('Unable to load policy catalog.', 'error')
    });
  }

  refreshReports() {
    this.adminApiService.getReports().subscribe({
      next: (reports) => {
        this.reports = reports;
      },
      error: () => this.showToast('Unable to load system reports.', 'error')
    });
  }

  refreshUsers() {
    this.adminApiService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: () => this.showToast('Unable to load users.', 'error')
    });
  }

  filteredClaims() {
    if (!this.searchQuery) {
      return this.claims;
    }

    const lower = this.searchQuery.toLowerCase();
    return this.claims.filter((claim) =>
      claim.claimantName?.toLowerCase().includes(lower) ||
      claim.claimNumber?.toLowerCase().includes(lower) ||
      String(claim.id).includes(lower)
    );
  }

  toggleRow(id: number) {
    this.expandedRow = this.expandedRow === id ? null : id;
  }

  reviewClaim(claimId: number, status: string) {
    if (this.reviewingClaimId) {
      return;
    }

    const action = status === 'APPROVED' ? 'approved' : 'rejected';
    const claim = this.claims.find((item) => item.id === claimId);
    const previousStatus = claim?.status;
    this.reviewingClaimId = claimId;
    this.applyClaimStatus(claimId, status);
    
    this.adminApiService.reviewClaim(claimId, status)
      .pipe(
        timeout(10000),
        catchError((err) => {
          if (claim && previousStatus) {
            this.applyClaimStatus(claimId, previousStatus);
          }
          const errorMsg = err?.error?.message || err?.message || 'Claim review failed. Please try again.';
          this.showToast(errorMsg, 'error');
          return of(null);
        }),
        finalize(() => {
          this.reviewingClaimId = null;
        })
      )
      .subscribe({
        next: (response) => {
          if (response !== null) {
            this.showToast(`Claim ${action} successfully.`, 'success');
          }
        }
      });
  }

  viewDocument(claim: ClaimRecord) {
    if (!claim.id) {
      this.showToast('Unable to identify this claim document.', 'error');
      return;
    }

    this.selectedClaim = claim;
    this.showDocModal = true;
    this.isDocumentLoading = true;
    this.documentLoadingClaimId = claim.id;
    this.revokeDocumentUrl();

    const url = this.adminApiService.getClaimDocumentPreviewUrl(claim.id);
    this.documentPreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    window.setTimeout(() => this.finishDocumentLoading(), 300);
  }

  onDocumentLoaded() {
    window.setTimeout(() => this.finishDocumentLoading(), 0);
  }

  onDocumentLoadFailed() {
    window.setTimeout(() => this.finishDocumentLoading(), 0);
    this.showToast('Unable to load document.', 'error');
  }

  closeDocumentModal() {
    this.showDocModal = false;
    this.selectedClaim = null;
    this.revokeDocumentUrl();
  }

  savePolicy() {
    if (!this.newPolicy.policyName || this.newPolicy.premium <= 0 || this.newPolicy.duration <= 0) {
      this.showToast('Please fill all policy fields with valid values.', 'error');
      return;
    }

    this.isPolicySaving = true;
    this.adminApiService.createPolicy(this.newPolicy)
      .pipe(finalize(() => {
        this.isPolicySaving = false;
      }))
      .subscribe({
        next: (policy) => {
          // Immediately add to UI
          this.policies.push(policy);
          this.showPolicyForm = false;
          this.newPolicy = { policyName: '', policyType: 'LIFE', premium: 0, duration: 1 };
          this.showToast(`Policy "${policy.policyName}" created successfully.`, 'success');
          // Refresh in background to ensure sync
          this.refreshPolicies();
        },
        error: (err) => {
          const errorMsg = err?.error?.message || err?.message || 'Failed to create policy';
          this.showToast(errorMsg, 'error');
        }
      });
  }

  requestDeletePolicy(policy: PolicyRecord) {
    this.pendingDeletePolicy = policy;
    this.showDeleteConfirm = true;
  }

  cancelDeletePolicy() {
    this.pendingDeletePolicy = null;
    this.showDeleteConfirm = false;
  }

  confirmDeletePolicy() {
    if (!this.pendingDeletePolicy) {
      return;
    }

    const policyName = this.pendingDeletePolicy.policyName;
    this.adminApiService.deletePolicy(this.pendingDeletePolicy.id).subscribe({
      next: () => {
        this.showToast(`Policy "${policyName}" deleted successfully.`, 'success');
        this.policies = this.policies.filter((policy) => policy.id !== this.pendingDeletePolicy?.id);
        this.cancelDeletePolicy();
      },
      error: (err) => {
        const errorMsg = err?.error?.message || err?.message || 'Failed to delete policy';
        this.showToast(errorMsg, 'error');
        this.cancelDeletePolicy();
      }
    });
  }

  showToast(message: string, type: 'success' | 'error') {
    this.toast = { show: true, message, type };
    setTimeout(() => {
      this.toast.show = false;
    }, 3000);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  private revokeDocumentUrl() {
    if (this.rawDocumentUrl) {
      URL.revokeObjectURL(this.rawDocumentUrl);
      this.rawDocumentUrl = null;
    }
    this.documentPreviewUrl = null;
  }

  private finishDocumentLoading() {
    this.isDocumentLoading = false;
    this.documentLoadingClaimId = null;
    this.cdr.detectChanges();
  }

  private applyClaimStatus(claimId: number, status: string) {
    const claim = this.claims.find((item) => item.id === claimId);
    if (!claim) {
      return;
    }

    const previousStatus = claim.status;
    claim.status = status;

    if (previousStatus === status) {
      return;
    }

    if (previousStatus === 'APPROVED') {
      this.reports.APPROVED = Math.max(0, this.reports.APPROVED - 1);
    }
    if (previousStatus === 'REJECTED') {
      this.reports.REJECTED = Math.max(0, this.reports.REJECTED - 1);
    }
    if (status === 'APPROVED') {
      this.reports.APPROVED += 1;
    }
    if (status === 'REJECTED') {
      this.reports.REJECTED += 1;
    }
  }
}
