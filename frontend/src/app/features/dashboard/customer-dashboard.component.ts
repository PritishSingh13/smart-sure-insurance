import { ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { catchError, finalize, switchMap, timeout } from 'rxjs/operators';
import { AuthApiService, UpdateProfilePayload, UserProfile } from '../auth/services/auth-api.service';
import { ClaimRecord, CustomerApiService, PolicyRecord } from '../../core/services/customer-api.service';

interface CustomerPolicyView extends PolicyRecord {
  coverageAmount: number;
  description: string;
}

type SubmitState = 'idle' | 'submitting' | 'submitted';

@Component({
  selector: 'app-customer-dashboard',
  templateUrl: './customer-dashboard.component.html',
  standalone: false
})
export class CustomerDashboardComponent implements OnInit, OnDestroy {
  @ViewChild('claimFileInput') claimFileInput?: ElementRef<HTMLInputElement>;

  activeTab: 'marketplace' | 'coverages' | 'claims' | 'profile' = 'marketplace';
  profile: UserProfile | null = null;
  editProfileModel: UpdateProfilePayload = {
    name: '',
    email: '',
    phone: '',
    address: '',
    profileImage: null
  };

  policies: CustomerPolicyView[] = [];
  myPolicies: CustomerPolicyView[] = [];
  myClaims: ClaimRecord[] = [];
  showChat = false;
  isInitialLoading = true;
  isProfileSaving = false;
  profileSaveState: 'idle' | 'saved' = 'idle';
  pendingProfileImagePreview: string | null = null;
  purchasingPolicyId: number | null = null;

  selectedPolicyId: number | null = null;
  selectedFile: File | null = null;
  selectedFileName = '';
  submitState: SubmitState = 'idle';
  submitMessage = 'Upload and Submit Claim';
  isClaimActionLocked = false;
  selectedClaimForView: ClaimRecord | null = null;
  showClaimDocumentModal = false;
  isClaimDocumentLoading = false;
  claimDocumentPreviewUrl: SafeResourceUrl | null = null;
  rawClaimDocumentUrl: string | null = null;

  toast = { show: false, message: '', type: 'success' as 'success' | 'error' };

  constructor(
    private authService: AuthApiService,
    private customerApiService: CustomerApiService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    this.loadDashboard();
  }

  ngOnDestroy() {
    this.revokeClaimDocumentUrl();
  }

  get displayName(): string {
    return this.profile?.name || 'Customer';
  }

  get avatarLabel(): string {
    return this.displayName.charAt(0).toUpperCase() || 'U';
  }

  get savedProfileImage(): string | null {
    return this.profile?.profileImage ?? null;
  }

  get editableProfileImage(): string | null {
    return this.pendingProfileImagePreview ?? this.editProfileModel.profileImage ?? null;
  }

  loadDashboard() {
    this.isInitialLoading = true;

    this.authService.getCurrentUser().pipe(
      timeout(5000),
      catchError(() => {
        this.showToast('Unable to load your account details.', 'error');
        return of(null);
      }),
      finalize(() => {
        this.isInitialLoading = false;
        this.cdr.detectChanges();
      })
    ).subscribe((profile) => {
        if (!profile) {
          this.authService.logout();
          this.router.navigate(['/']);
          return;
        }

        this.setProfile(profile);
        this.loadCustomerData();
      });
  }

  private loadCustomerData() {
    this.customerApiService.getPolicies().pipe(
      timeout(6000),
      catchError(() => {
        this.showToast('Unable to load available policies.', 'error');
        return of([]);
      })
    ).subscribe((policies) => {
      this.policies = policies.map((policy) => this.toPolicyView(policy));
      this.cdr.detectChanges();
    });

    this.customerApiService.getMyPolicies().pipe(
      timeout(6000),
      catchError(() => {
        this.showToast('Unable to load your purchased policies.', 'error');
        return of([]);
      })
    ).subscribe((myPolicies) => {
      this.myPolicies = myPolicies.map((policy) => this.toPolicyView(policy));
      if (!this.selectedPolicyId && this.myPolicies.length > 0) {
        this.selectedPolicyId = this.myPolicies[0].id;
      }
      this.cdr.detectChanges();
    });

    this.customerApiService.getMyClaims().pipe(
      timeout(6000),
      catchError(() => {
        this.showToast('Unable to load your claims.', 'error');
        return of([]);
      })
    ).subscribe((myClaims) => {
      this.myClaims = myClaims;
      this.cdr.detectChanges();
    });
  }

  private setProfile(profile: UserProfile) {
    this.profile = profile;
    this.pendingProfileImagePreview = null;
    this.editProfileModel = {
      name: profile.name,
      email: profile.email,
      phone: profile.phone,
      address: profile.address,
      profileImage: profile.profileImage ?? null
    };
  }

  fetchPolicies() {
    this.customerApiService.getPolicies().subscribe({
      next: (policies) => {
        this.policies = policies.map((policy) => this.toPolicyView(policy));
        this.cdr.detectChanges();
      },
      error: () => {
        this.showToast('Unable to load available policies.', 'error');
        this.cdr.detectChanges();
      }
    });
  }

  fetchMyPolicies() {
    this.customerApiService.getMyPolicies().subscribe({
      next: (policies) => {
        this.myPolicies = policies.map((policy) => this.toPolicyView(policy));
        if (!this.selectedPolicyId && this.myPolicies.length > 0) {
          this.selectedPolicyId = this.myPolicies[0].id;
        }
        this.cdr.detectChanges();
      },
      error: () => this.showToast('Unable to load your purchased policies.', 'error')
    });
  }

  fetchMyClaims() {
    this.customerApiService.getMyClaims().subscribe({
      next: (claims) => {
        this.myClaims = claims;
        this.cdr.detectChanges();
      },
      error: () => this.showToast('Unable to load your claims.', 'error')
    });
  }

  purchasePolicy(policyId: number, policyName: string) {
    if (this.purchasingPolicyId) {
      return;
    }

    this.purchasingPolicyId = policyId;
    this.customerApiService.purchasePolicy(policyId)
      .pipe(finalize(() => {
        this.purchasingPolicyId = null;
      }))
      .subscribe({
        next: () => {
          this.showToast(`"${policyName}" purchased successfully.`, 'success');
          this.fetchMyPolicies();
          this.activeTab = 'coverages';
        },
        error: (err) => {
          const errorMsg = err?.error?.message || err?.message || 'Policy purchase failed';
          this.showToast(errorMsg, 'error');
        }
      });
  }

  goToProfile() {
    this.activeTab = 'profile';
  }

  goToMarketplace() {
    this.activeTab = 'marketplace';
  }

  playLogoPulse() {
    this.showToast('SmartSure customer portal is live.', 'success');
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
    this.selectedFileName = this.selectedFile?.name ?? '';
  }

  submitClaim() {
    if (!this.selectedPolicyId || !this.selectedFile || !this.profile?.name || this.isClaimActionLocked) {
      this.showToast('Select a policy and choose a PDF before submitting the claim.', 'error');
      return;
    }

    this.submitState = 'submitting';
    this.submitMessage = 'Submitting...';
    this.isClaimActionLocked = true;

    this.customerApiService.uploadClaim(this.selectedFile, this.selectedPolicyId, this.profile.name)
      .pipe(
        switchMap((claim) => this.customerApiService.initiateClaim(claim.claimNumber))
      )
      .subscribe({
        next: (claim) => {
          this.selectedFile = null;
          this.selectedFileName = '';
          if (this.claimFileInput?.nativeElement) {
            this.claimFileInput.nativeElement.value = '';
          }
          this.submitState = 'submitted';
          this.submitMessage = 'Submitted';
          this.showToast(`Claim submitted successfully. Claim number: ${claim.claimNumber}`, 'success');
          this.fetchMyClaims();
          setTimeout(() => {
            this.submitState = 'idle';
            this.submitMessage = 'Upload and Submit Claim';
            this.isClaimActionLocked = false;
          }, 1400);
        },
        error: () => {
          this.submitState = 'idle';
          this.submitMessage = 'Upload and Submit Claim';
          this.isClaimActionLocked = false;
          this.showToast('Claim submission failed. Only PDF files are accepted by the backend right now.', 'error');
        }
      });
  }

  onProfileImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      return;
    }

    this.resizeProfileImage(file)
      .then((image) => {
        this.pendingProfileImagePreview = image;
        this.editProfileModel.profileImage = image;
        this.profileSaveState = 'idle';
        this.cdr.detectChanges();
      })
      .catch(() => {
        this.showToast('Unable to read this image. Please choose another photo.', 'error');
      });
  }

  saveProfile() {
    if (this.isProfileSaving) {
      return;
    }

    this.isProfileSaving = true;
    this.profileSaveState = 'idle';
    this.authService.updateCurrentUser(this.editProfileModel).pipe(
      finalize(() => {
        this.isProfileSaving = false;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (response) => {
        this.setProfile(response.user);
        this.profileSaveState = 'saved';
        this.showToast('Details updated properly.', 'success');
      },
      error: (error) => {
        const message = typeof error?.error === 'string' ? error.error : 'Profile update failed.';
        this.showToast(message, 'error');
      }
    });
  }

  private resizeProfileImage(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onerror = () => reject(new Error('Unable to read image'));
      reader.onload = () => {
        const image = new Image();
        image.onerror = () => reject(new Error('Unable to load image'));
        image.onload = () => {
          const maxSize = 512;
          const scale = Math.min(maxSize / image.width, maxSize / image.height, 1);
          const canvas = document.createElement('canvas');
          canvas.width = Math.max(1, Math.round(image.width * scale));
          canvas.height = Math.max(1, Math.round(image.height * scale));

          const context = canvas.getContext('2d');
          if (!context) {
            reject(new Error('Unable to prepare image'));
            return;
          }

          context.drawImage(image, 0, 0, canvas.width, canvas.height);
          resolve(canvas.toDataURL('image/jpeg', 0.82));
        };
        image.src = String(reader.result);
      };
      reader.readAsDataURL(file);
    });
  }

  discardProfileEdits() {
    if (this.profile) {
      this.setProfile(this.profile);
      this.cdr.detectChanges();
    }
  }

  viewClaimDocument(claim: ClaimRecord) {
    this.selectedClaimForView = claim;
    this.showClaimDocumentModal = true;
    this.isClaimDocumentLoading = true;
    this.revokeClaimDocumentUrl();

    this.customerApiService.getClaimDocument(claim.claimNumber).pipe(
      timeout(12000),
      finalize(() => {
        this.isClaimDocumentLoading = false;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (blob) => {
        const pdfBlob = blob.type === 'application/pdf' ? blob : new Blob([blob], { type: 'application/pdf' });
        const url = URL.createObjectURL(pdfBlob);
        this.rawClaimDocumentUrl = url;
        this.claimDocumentPreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
      },
      error: () => {
        this.showToast('Unable to open this claim document.', 'error');
      }
    });
  }

  closeClaimDocumentModal() {
    this.showClaimDocumentModal = false;
    this.selectedClaimForView = null;
    this.revokeClaimDocumentUrl();
  }

  showToast(message: string, type: 'success' | 'error') {
    this.toast = { show: true, message, type };
    setTimeout(() => {
      this.toast.show = false;
    }, 2600);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  private toPolicyView(policy: PolicyRecord): CustomerPolicyView {
    const coverageAmount = Math.round(policy.premium * Math.max(policy.duration, 1) * 120);
    const descriptions: Record<string, string> = {
      LIFE: 'Long-term protection designed for family and income security.',
      AUTO: 'Vehicle protection for collision, theft, and on-road emergencies.',
      HEALTH: 'Medical coverage for planned and unexpected healthcare needs.',
      HOME: 'Property coverage for home structure, contents, and liability.'
    };

    return {
      ...policy,
      coverageAmount,
      description: descriptions[policy.policyType] ?? 'Flexible insurance coverage for everyday protection.'
    };
  }

  private revokeClaimDocumentUrl() {
    if (this.rawClaimDocumentUrl) {
      URL.revokeObjectURL(this.rawClaimDocumentUrl);
      this.rawClaimDocumentUrl = null;
    }
    this.claimDocumentPreviewUrl = null;
  }
}
