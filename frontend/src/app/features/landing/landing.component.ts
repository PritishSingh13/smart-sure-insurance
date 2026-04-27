import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthApiService } from '../auth/services/auth-api.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  standalone: false
})
export class LandingComponent implements OnInit {
  // UI State
  showAuthModal = false;
  isLoginMode = true; 
  isLoggedIn = false;
  
  // Forms
  loginForm: FormGroup;
  registerForm: FormGroup;
  
  isLoading = false;
  authError: string | null = null;

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
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('^\\d{10}$')]],
      address: [''],
      role: ['CUSTOMER', Validators.required] // Allows choosing Admin or Customer
    });
  }

  ngOnInit() {
    if (this.authService.getToken()) {
      this.isLoggedIn = true;
    }
  }

  // --- UI Triggers ---
  openAuthModal() {
    if (this.isLoggedIn) {
      // Auto redirect since logged in
      const role = this.authService.getRole();
      if (role === 'ADMIN') this.router.navigate(['/dashboard/admin']);
      else this.router.navigate(['/dashboard/customer']);
      return;
    }
    this.showAuthModal = true;
    this.authError = null;
  }

  closeAuthModal() {
    this.showAuthModal = false;
  }

  toggleAuthMode() {
    this.isLoginMode = !this.isLoginMode;
    this.authError = null;
  }

  logout() {
    this.authService.logout();
    this.isLoggedIn = false;
  }

  // --- API Handlers ---
  onSubmitLogin() {
    if (this.loginForm.invalid) return;
    
    this.isLoading = true;
    this.authError = null;

    this.authService.login(this.loginForm.value).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.isLoggedIn = true;
        this.closeAuthModal();
        if (res.role === 'ADMIN') this.router.navigate(['/dashboard/admin']);
        else this.router.navigate(['/dashboard/customer']);
      },
      error: (err) => {
        this.isLoading = false;
        this.authError = 'Invalid credentials. User might not exist or password mismatch.';
      }
    });
  }

  onSubmitRegister() {
    if (this.registerForm.invalid) return;

    this.isLoading = true;
    this.authError = null;

    this.authService.register(this.registerForm.value).subscribe({
      next: (res) => {
        // Auto Login After Registration
        this.authService.login({
          email: this.registerForm.value.email,
          password: this.registerForm.value.password
        }).subscribe(loginRes => {
           this.isLoading = false;
           this.isLoggedIn = true;
           this.closeAuthModal();
           if (loginRes.role === 'ADMIN') this.router.navigate(['/dashboard/admin']);
           else this.router.navigate(['/dashboard/customer']);
        });
      },
      error: (err) => {
        this.isLoading = false;
        this.authError = 'Registration failed. Email might already exist.';
      }
    });
  }
}
