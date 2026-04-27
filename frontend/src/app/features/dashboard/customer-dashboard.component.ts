import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthApiService } from '../auth/services/auth-api.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-customer-dashboard',
  template: `
    <div class="min-h-screen bg-gray-50 flex font-sans overflow-hidden">
      
      <!-- Modern Sidebar -->
      <aside class="w-72 bg-white shadow-[10px_0_20px_rgba(0,0,0,0.03)] h-screen flex flex-col z-20 relative border-r border-gray-100">
        <div class="p-8 border-b border-gray-100 flex items-center cursor-pointer hover:opacity-80 transition-opacity" (click)="activeTab = 'policies'">
          <span class="font-black text-3xl uppercase italic tracking-widest text-[#c81921] drop-shadow-sm">SMARTSURE</span>
        </div>
        
        <nav class="flex-grow p-6 space-y-2 overflow-y-auto">
          <div class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-4 ml-2">Menu</div>
          
          <a href="javascript:void(0)" (click)="activeTab = 'dashboard'" [ngClass]="getTabClass('dashboard')" class="flex items-center space-x-4 w-full px-4 py-3.5 rounded-2xl transition-all duration-300 relative group">
            <div *ngIf="activeTab === 'dashboard'" class="absolute left-0 w-1.5 h-8 bg-[#c81921] rounded-r-full"></div>
            <svg class="w-5 h-5 transition-transform group-hover:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>
            <span class="font-semibold tracking-wide">Overview</span>
          </a>
          
          <a href="javascript:void(0)" (click)="activeTab = 'policies'" [ngClass]="getTabClass('policies')" class="flex items-center space-x-4 w-full px-4 py-3.5 rounded-2xl transition-all duration-300 relative group">
            <div *ngIf="activeTab === 'policies'" class="absolute left-0 w-1.5 h-8 bg-[#c81921] rounded-r-full"></div>
            <svg class="w-5 h-5 transition-transform group-hover:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path></svg>
            <span class="font-semibold tracking-wide">Purchase Policy</span>
          </a>

          <a href="javascript:void(0)" (click)="activeTab = 'my-policies'" [ngClass]="getTabClass('my-policies')" class="flex items-center space-x-4 w-full px-4 py-3.5 rounded-2xl transition-all duration-300 relative group">
            <div *ngIf="activeTab === 'my-policies'" class="absolute left-0 w-1.5 h-8 bg-[#c81921] rounded-r-full"></div>
            <svg class="w-5 h-5 transition-transform group-hover:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            <span class="font-semibold tracking-wide">My Policies</span>
          </a>

          <a href="javascript:void(0)" (click)="activeTab = 'claims'" [ngClass]="getTabClass('claims')" class="flex items-center space-x-4 w-full px-4 py-3.5 rounded-2xl transition-all duration-300 relative group">
            <div *ngIf="activeTab === 'claims'" class="absolute left-0 w-1.5 h-8 bg-[#c81921] rounded-r-full"></div>
            <svg class="w-5 h-5 transition-transform group-hover:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
            <span class="font-semibold tracking-wide">Claims</span>
          </a>

          <div class="mt-8 mb-4">
             <div class="text-xs font-bold text-gray-400 uppercase tracking-wider ml-2">Settings</div>
          </div>

          <a href="javascript:void(0)" (click)="activeTab = 'profile'" [ngClass]="getTabClass('profile')" class="flex items-center space-x-4 w-full px-4 py-3.5 rounded-2xl transition-all duration-300 relative group">
            <div *ngIf="activeTab === 'profile'" class="absolute left-0 w-1.5 h-8 bg-[#c81921] rounded-r-full"></div>
            <svg class="w-5 h-5 transition-transform group-hover:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
            <span class="font-semibold tracking-wide">Profile</span>
          </a>
        </nav>
        
        <!-- User Profile area -->
        <div class="p-6 border-t border-gray-100 bg-gray-50/50">
           <div class="flex items-center space-x-4 mb-5 bg-white p-3 rounded-2xl shadow-sm border border-gray-100">
             <div class="w-12 h-12 rounded-full bg-gradient-to-br from-[#c81921] to-[#ff4d4d] text-white flex items-center justify-center font-bold text-xl shadow-md">
               {{ email ? email[0].toUpperCase() : 'U' }}
             </div>
             <div class="overflow-hidden">
               <p class="text-sm font-bold text-gray-800 truncate">{{ email }}</p>
               <p class="text-xs text-[#c81921] font-semibold">Premium Member</p>
             </div>
           </div>
           <button (click)="logout()" class="w-full bg-white border border-gray-200 text-gray-700 hover:text-white hover:bg-[#c81921] hover:border-[#c81921] px-4 py-3 rounded-2xl font-bold shadow-sm transition-all duration-300 flex justify-center items-center space-x-2 group">
             <svg class="w-5 h-5 transition-transform group-hover:-translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path></svg>
             <span>Log Out</span>
           </button>
        </div>
      </aside>

      <!-- Main Content Area -->
      <main class="flex-grow h-screen overflow-y-auto bg-gradient-to-br from-gray-50 via-gray-50 to-red-50/30 relative">
        
        <!-- Header -->
        <header class="w-full py-6 px-10 flex justify-between items-center z-10 sticky top-0 bg-white/70 backdrop-blur-xl border-b border-gray-200/50 shadow-sm transition-all">
           <div>
             <h1 class="text-3xl font-black text-gray-800 tracking-tight flex items-center space-x-3">
               <span *ngIf="activeTab === 'dashboard'">Overview</span>
               <span *ngIf="activeTab === 'policies'">Policy Market</span>
               <span *ngIf="activeTab === 'my-policies'">My Vault</span>
               <span *ngIf="activeTab === 'claims'">Claims Center</span>
               <span *ngIf="activeTab === 'profile'">Account Profile</span>
             </h1>
             <p class="text-sm text-gray-500 font-medium mt-1">Manage your life, securely and easily.</p>
           </div>
           
           <div class="flex items-center space-x-4">
              <button class="w-12 h-12 rounded-full bg-white border border-gray-200 text-gray-500 hover:text-[#c81921] hover:shadow-lg hover:-translate-y-0.5 flex items-center justify-center shadow-sm transition-all duration-300 relative">
                <span class="absolute top-3 right-3 w-2.5 h-2.5 bg-[#c81921] rounded-full animate-pulse border-2 border-white"></span>
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path></svg>
              </button>
           </div>
        </header>

        <!-- Dynamic Content Body -->
        <div class="p-10 max-w-7xl mx-auto w-full">
          
          <!-- Policies View -->
          <div *ngIf="activeTab === 'policies'" class="opacity-0 animate-fade-in-up" style="animation-fill-mode: forwards;">
             
             <!-- Banner -->
             <div class="w-full bg-gradient-to-r from-[#c81921] to-[#800f15] rounded-3xl p-8 mb-10 text-white shadow-[0_20px_40px_rgba(200,25,33,0.3)] relative overflow-hidden flex items-center justify-between group">
               <div class="absolute top-0 right-0 w-64 h-64 bg-white opacity-5 rounded-full blur-3xl transform translate-x-1/2 -translate-y-1/2 group-hover:scale-150 transition-transform duration-700"></div>
               <div class="relative z-10 max-w-xl">
                 <h2 class="text-3xl font-black mb-2 drop-shadow-md">Secure Your Future Today</h2>
                 <p class="text-red-100 font-medium">Explore our top-tier insurance policies designed to give you peace of mind with instant digital coverage.</p>
               </div>
               <div class="relative z-10 hidden md:block">
                 <div class="w-24 h-24 bg-white/20 backdrop-blur-md rounded-full flex items-center justify-center border border-white/30 shadow-xl">
                   <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"></path></svg>
                 </div>
               </div>
             </div>

             <!-- Policy Cards Grid -->
             <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                <div *ngFor="let policy of policies; let i = index" 
                     class="bg-white rounded-3xl shadow-[0_10px_30px_rgba(0,0,0,0.04)] p-8 border border-gray-100 hover:-translate-y-3 hover:shadow-[0_20px_40px_rgba(200,25,33,0.12)] transition-all duration-500 opacity-0 animate-fade-in-up relative overflow-hidden group"
                     [ngStyle]="{'animation-delay': (i * 0.1) + 's', 'animation-fill-mode': 'forwards'}">
                   
                   <div class="absolute top-0 left-0 w-full h-1.5 bg-gradient-to-r from-[#c81921] to-[#ff4d4d] transform origin-left scale-x-0 group-hover:scale-x-100 transition-transform duration-500"></div>

                   <div class="w-14 h-14 bg-red-50 rounded-2xl flex items-center justify-center text-[#c81921] font-bold text-2xl mb-6 shadow-sm border border-red-100 group-hover:rotate-6 transition-transform duration-300">
                     <ng-container *ngIf="i === 0">💎</ng-container>
                     <ng-container *ngIf="i === 1">🚗</ng-container>
                     <ng-container *ngIf="i === 2">❤️</ng-container>
                   </div>
                   
                   <h2 class="text-2xl font-black text-gray-900 mb-3">{{ policy.name }}</h2>
                   <p class="text-gray-500 mb-8 min-h-[48px] leading-relaxed text-sm">{{ policy.description }}</p>
                   
                   <div class="space-y-4 mb-8">
                     <div class="flex justify-between items-center bg-gray-50/80 p-4 rounded-2xl border border-gray-100">
                       <span class="text-xs text-gray-400 font-bold uppercase tracking-wider flex items-center"><svg class="w-4 h-4 mr-1 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg> Coverage</span>
                       <span class="font-black text-gray-800 text-lg">\${{ policy.coverageAmount | number }}</span>
                     </div>
                     <div class="flex justify-between items-center bg-red-50/50 p-4 rounded-2xl border border-red-100">
                       <span class="text-xs text-red-400 font-bold uppercase tracking-wider flex items-center"><svg class="w-4 h-4 mr-1 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg> Premium</span>
                       <span class="font-black text-[#c81921] text-lg">\${{ policy.premium }}<span class="text-xs text-red-300">/mo</span></span>
                     </div>
                   </div>

                   <button (click)="purchasePolicy(policy.id)" class="w-full bg-white border-2 border-[#c81921] text-[#c81921] hover:bg-[#c81921] hover:text-white font-black py-3.5 rounded-2xl transition-all duration-300 shadow-sm hover:shadow-xl flex items-center justify-center space-x-2">
                     <span>Get Covered</span>
                     <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path></svg>
                   </button>
                </div>
             </div>
          </div>

          <!-- Placeholder Views for other tabs -->
          <div *ngIf="activeTab !== 'policies'" class="flex flex-col items-center justify-center h-[65vh] opacity-0 animate-fade-in-up" style="animation-delay: 0.1s; animation-fill-mode: forwards;">
             <div class="relative w-32 h-32 mb-8 group">
               <div class="absolute inset-0 bg-red-100 rounded-full blur-xl group-hover:blur-2xl transition-all duration-500 opacity-60"></div>
               <div class="relative bg-white w-full h-full rounded-full shadow-xl flex items-center justify-center border border-gray-100">
                 <svg class="w-14 h-14 text-[#c81921]" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"></path></svg>
               </div>
             </div>
             <h2 class="text-3xl font-black text-gray-800 mb-3 tracking-tight">Module Under Construction</h2>
             <p class="text-gray-500 text-center max-w-md text-lg leading-relaxed mb-8">The <span class="font-bold text-[#c81921]">{{ activeTab | uppercase }}</span> interface is currently being polished by our design team.</p>
             <button (click)="activeTab = 'policies'" class="bg-gray-900 text-white px-8 py-3.5 rounded-2xl font-bold shadow-[0_10px_20px_rgba(0,0,0,0.1)] hover:-translate-y-1 hover:shadow-[0_15px_30px_rgba(0,0,0,0.2)] transition-all duration-300">
               Return to Market
             </button>
          </div>

        </div>
      </main>
    </div>
  `,
  standalone: false
})
export class CustomerDashboardComponent implements OnInit {
  
  email: string | null = '';
  policies: any[] = [];
  activeTab: string = 'policies';

  constructor(
    private http: HttpClient,
    private authService: AuthApiService,
    private router: Router
  ) {}

  ngOnInit() {
    this.email = localStorage.getItem('smartsure_email') || 'Valued Customer';
    this.fetchPolicies();
  }

  fetchPolicies() {
    // MOCKED DATA FOR UI PREVIEW
    this.policies = [
        { id: 1, name: 'Smart Life Elite', description: 'Comprehensive lifetime coverage with zero deductible. Ensure your legacy is protected.', coverageAmount: 500000, premium: 149 },
        { id: 2, name: 'AutoSecure Pro', description: 'Total loss and collision repair for luxury vehicles. Drive with complete peace of mind.', coverageAmount: 85000, premium: 89 },
        { id: 3, name: 'Health Shield', description: '24/7 access to priority medical care, specialists, and zero-wait emergency services.', coverageAmount: 250000, premium: 299 }
    ];
  }

  purchasePolicy(policyId: number) {
    alert('Mock Action: Successfully initiated purchase for Policy ID ' + policyId);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  getTabClass(tabName: string) {
    if (this.activeTab === tabName) {
      return 'bg-red-50/80 text-[#c81921] shadow-sm font-bold';
    }
    return 'text-gray-500 hover:bg-gray-50 hover:text-gray-800 font-medium';
  }
}
