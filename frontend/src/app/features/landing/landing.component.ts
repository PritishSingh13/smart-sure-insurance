import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthApiService } from '../auth/services/auth-api.service';
import { finalize, timeout } from 'rxjs/operators';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  standalone: false
})
export class LandingComponent implements OnInit {
  showAuthModal = false;
  showQuoteModal = false;
  showSupportChat = false;
  isLoginMode = true;
  isLoggedIn = false;
  showLoginPassword = false;
  showRegisterPassword = false;

  countryCodes = [
    { name: 'India', code: '+91' },
    { name: 'United States', code: '+1' },
    { name: 'United Kingdom', code: '+44' },
    { name: 'Canada', code: '+1' },
    { name: 'Australia', code: '+61' },
    { name: 'United Arab Emirates', code: '+971' },
    { name: 'Singapore', code: '+65' },
    { name: 'Germany', code: '+49' },
    { name: 'France', code: '+33' },
    { name: 'Japan', code: '+81' }
  ];

  loginForm: FormGroup;
  registerForm: FormGroup;

  isLoading = false;
  authError: string | null = null;
  registerSuccess = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthApiService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      phoneCountryCode: ['+91', Validators.required],
      phoneNumber: ['', [Validators.required, Validators.pattern('^[0-9]{6,14}$')]],
      address: ['', [Validators.required, Validators.minLength(5)]],
      role: ['CUSTOMER']
    });
  }

  ngOnInit() {
    this.isLoggedIn = !!this.authService.getToken();
  }

  openLoginModal() {
    if (this.isLoggedIn) { this.redirectByRole(); return; }
    this.isLoginMode = true;
    this.showAuthModal = true;
    this.authError = null;
  }

  openRegisterModal() {
    if (this.isLoggedIn) { this.redirectByRole(); return; }
    this.isLoginMode = false;
    this.showAuthModal = true;
    this.authError = null;
    this.registerSuccess = false;
  }

  closeAuthModal() {
    this.showAuthModal = false;
    this.authError = null;
    this.registerSuccess = false;
  }

  openQuoteModal() {
    if (this.isLoggedIn) { this.redirectByRole(); return; }
    this.showQuoteModal = true;
  }

  closeQuoteModal() {
    this.showQuoteModal = false;
  }

  selectQuoteType(_type: string) {
    this.closeQuoteModal();
    this.openRegisterModal();
  }

  toggleSupportChat() {
    this.showSupportChat = !this.showSupportChat;
  }

  toggleAuthMode() {
    this.isLoginMode = !this.isLoginMode;
    this.authError = null;
    this.registerSuccess = false;
  }

  toggleLoginPasswordVisibility() {
    this.showLoginPassword = !this.showLoginPassword;
  }

  toggleRegisterPasswordVisibility() {
    this.showRegisterPassword = !this.showRegisterPassword;
  }

  logout() {
    this.authService.logout();
    this.isLoggedIn = false;
  }

  redirectByRolePublic() {
    this.redirectByRole();
  }

  onSubmitLogin() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.authError = null;

    this.authService.login(this.loginForm.getRawValue()).pipe(
      timeout(10000),
      finalize(() => {
        this.isLoading = false;
      })
    ).subscribe({
      next: (response) => {
        this.isLoggedIn = true;
        this.closeAuthModal();
        this.redirectByRole(response.role);
      },
      error: (error) => {
        this.authError = this.extractError(error, 'Sign in failed. Please check your email and password.');
      }
    });
  }

  onSubmitRegister() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.authError = null;

    const formValue = this.registerForm.getRawValue();
    const payload = {
      name: formValue.name,
      email: formValue.email,
      password: formValue.password,
      phone: `${formValue.phoneCountryCode} ${formValue.phoneNumber}`,
      address: formValue.address,
      role: 'CUSTOMER'
    };

    this.authService.register(payload).pipe(
      timeout(10000),
      finalize(() => {
        this.isLoading = false;
      })
    ).subscribe({
      next: (message) => {
        if (message.toLowerCase().includes('already exists')) {
          this.authError = 'This email address is already registered. Please sign in instead.';
          return;
        }

        this.registerSuccess = true;
        this.isLoginMode = true;
        this.loginForm.patchValue({ email: payload.email, password: '' });
        this.registerForm.reset({
          name: '',
          email: '',
          password: '',
          phoneCountryCode: '+91',
          phoneNumber: '',
          address: '',
          role: 'CUSTOMER'
        });
      },
      error: (error) => {
        this.authError = this.extractError(error, 'Registration failed. Please try again.');
      }
    });
  }

  redirectByRole(role?: string | null) {
    const effectiveRole = String(role ?? this.authService.getRole() ?? '').toUpperCase();
    if (effectiveRole === 'ADMIN') {
      this.router.navigate(['/dashboard/admin']);
      return;
    }
    this.router.navigate(['/dashboard/customer']);
  }

  private extractError(error: unknown, fallback: string): string {
    const httpError = error as { error?: unknown };
    if (typeof httpError?.error === 'string' && httpError.error.trim()) {
      return httpError.error;
    }
    return fallback;
  }
}
