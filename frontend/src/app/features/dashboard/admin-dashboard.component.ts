import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthApiService } from '../auth/services/auth-api.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  template: `
    <div class="min-h-screen bg-[#0a0a0a] flex font-sans text-gray-200 overflow-hidden">
      
      <!-- Premium Dark Sidebar -->
      <aside class="w-72 bg-gray-950/80 backdrop-blur-2xl border-r border-gray-800/50 h-screen flex flex-col z-20 relative shadow-[10px_0_30px_rgba(0,0,0,0.5)]">
        <div class="p-8 border-b border-gray-800/50 flex items-center cursor-pointer hover:opacity-80 transition-opacity">
          <span class="font-black text-2xl uppercase tracking-widest text-[#E11D48] drop-shadow-[0_0_10px_rgba(225,29,72,0.3)]">SMARTSURE<br><span class="text-white text-lg">COMMAND</span></span>
        </div>
        
        <nav class="flex-grow p-6 space-y-2 overflow-y-auto">
          <div class="text-xs font-black text-gray-600 uppercase tracking-widest mb-4 ml-2">Administration</div>
          
          <a href="javascript:void(0)" (click)="activeTab = 'dashboard'" [ngClass]="getTabClass('dashboard')" class="flex items-center space-x-4 w-full px-4 py-3.5 rounded-xl transition-all duration-300 relative group">
            <div *ngIf="activeTab === 'dashboard'" class="absolute left-0 w-1 h-8 bg-[#E11D48] rounded-r-full shadow-[0_0_10px_rgba(225,29,72,0.8)]"></div>
            <svg class="w-5 h-5 transition-transform group-hover:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>
            <span class="font-semibold tracking-wide">Overview</span>
          </a>

          <a href="javascript:void(0)" (click)="activeTab = 'claims'" [ngClass]="getTabClass('claims')" class="flex items-center space-x-4 w-full px-4 py-3.5 rounded-xl transition-all duration-300 relative group">
            <div *ngIf="activeTab === 'claims'" class="absolute left-0 w-1 h-8 bg-[#E11D48] rounded-r-full shadow-[0_0_10px_rgba(225,29,72,0.8)]"></div>
            <svg class="w-5 h-5 transition-transform group-hover:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
            <span class="font-semibold tracking-wide">Claims Queue</span>
            <span *ngIf="pendingClaimsCount() > 0" class="ml-auto bg-[#E11D48] text-white text-xs font-black px-2 py-0.5 rounded-full shadow-[0_0_8px_rgba(225,29,72,0.6)]">{{ pendingClaimsCount() }}</span>
          </a>

          <a href="javascript:void(0)" (click)="activeTab = 'users'" [ngClass]="getTabClass('users')" class="flex items-center space-x-4 w-full px-4 py-3.5 rounded-xl transition-all duration-300 relative group">
            <div *ngIf="activeTab === 'users'" class="absolute left-0 w-1 h-8 bg-[#E11D48] rounded-r-full shadow-[0_0_10px_rgba(225,29,72,0.8)]"></div>
            <svg class="w-5 h-5 transition-transform group-hover:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
            <span class="font-semibold tracking-wide">User Mgmt</span>
          </a>

          <a href="javascript:void(0)" (click)="activeTab = 'policies'" [ngClass]="getTabClass('policies')" class="flex items-center space-x-4 w-full px-4 py-3.5 rounded-xl transition-all duration-300 relative group">
            <div *ngIf="activeTab === 'policies'" class="absolute left-0 w-1 h-8 bg-[#E11D48] rounded-r-full shadow-[0_0_10px_rgba(225,29,72,0.8)]"></div>
            <svg class="w-5 h-5 transition-transform group-hover:scale-110" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 002-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path></svg>
            <span class="font-semibold tracking-wide">All Policies</span>
          </a>
        </nav>
        
        <!-- Bottom Action Area -->
        <div class="p-6 border-t border-gray-800/50 bg-gray-950/30">
           <div class="flex items-center space-x-4 mb-5 bg-gray-900/80 p-3 rounded-xl border border-gray-800">
             <div class="w-10 h-10 rounded-full bg-gradient-to-br from-gray-700 to-gray-900 border border-gray-600 flex items-center justify-center font-bold text-white shadow-inner">
               <svg class="w-5 h-5 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
             </div>
             <div class="overflow-hidden">
               <p class="text-sm font-bold text-gray-200 truncate">SYSTEM ADMIN</p>
               <p class="text-xs text-green-400 font-mono tracking-widest">ONLINE</p>
             </div>
           </div>
           <button (click)="logout()" class="w-full bg-transparent border border-gray-700 text-gray-400 hover:text-[#E11D48] hover:border-[#E11D48] hover:bg-rose-950/20 px-4 py-3 rounded-xl font-bold shadow-sm transition-all duration-300 flex justify-center items-center space-x-2 group">
             <svg class="w-5 h-5 transition-transform group-hover:-translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path></svg>
             <span>Terminate Session</span>
           </button>
        </div>
      </aside>

      <!-- Main Content Area -->
      <main class="flex-grow h-screen overflow-y-auto bg-[radial-gradient(ellipse_at_top_right,_var(--tw-gradient-stops))] from-gray-900 via-[#0a0a0a] to-black relative">
        
        <!-- Glowing background orb -->
        <div class="absolute top-0 right-0 w-[500px] h-[500px] bg-rose-900/10 rounded-full blur-[100px] pointer-events-none"></div>

        <!-- Header -->
        <header class="w-full py-6 px-10 flex justify-between items-center z-10 sticky top-0 bg-gray-950/40 backdrop-blur-xl border-b border-gray-800/50 shadow-md">
           <div>
             <h1 class="text-3xl font-black text-white tracking-tight flex items-center space-x-3 drop-shadow-md">
               <span *ngIf="activeTab === 'dashboard'">Command Overview</span>
               <span *ngIf="activeTab === 'claims'">Claims Processing Center</span>
               <span *ngIf="activeTab === 'users'">User Management</span>
               <span *ngIf="activeTab === 'policies'">Global Policies</span>
             </h1>
             <p class="text-sm text-gray-400 font-medium mt-1 font-mono tracking-wide">System Status: <span class="text-green-400">Optimal</span></p>
           </div>
           
           <div class="flex items-center space-x-4">
              <button *ngIf="activeTab === 'claims'" (click)="fetchClaims()" class="bg-[#E11D48] text-white px-5 py-2.5 rounded-lg font-bold shadow-[0_0_15px_rgba(225,29,72,0.4)] hover:bg-rose-700 hover:shadow-[0_0_20px_rgba(225,29,72,0.6)] transition-all duration-300 flex items-center space-x-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path></svg>
                <span>Sync Data</span>
              </button>
           </div>
        </header>

        <!-- Dynamic Content Body -->
        <div class="p-10 max-w-7xl mx-auto w-full relative z-10">
          
          <!-- Claims View -->
          <div *ngIf="activeTab === 'claims'" class="opacity-0 animate-fade-in-up" style="animation-fill-mode: forwards;">
             
             <!-- Glassmorphism Table Container -->
             <div class="bg-gray-900/40 backdrop-blur-md rounded-3xl shadow-[0_20px_50px_rgba(0,0,0,0.5)] overflow-hidden border border-gray-700/50 relative group">
                
                <!-- Glowing Top Border -->
                <div class="absolute top-0 left-0 w-full h-0.5 bg-gradient-to-r from-transparent via-[#E11D48] to-transparent opacity-50 group-hover:opacity-100 transition-opacity duration-500"></div>

                <div class="overflow-x-auto">
                  <table class="w-full text-left border-collapse">
                    <thead>
                      <tr class="bg-gray-950/60 text-gray-400 text-xs uppercase tracking-widest font-black border-b border-gray-800">
                        <th class="py-5 px-6">Claim Reference</th>
                        <th class="py-5 px-6">Policy Link</th>
                        <th class="py-5 px-6">Claimant Identity</th>
                        <th class="py-5 px-6">Status Indicator</th>
                        <th class="py-5 px-6 text-right">Execution</th>
                      </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-800/50">
                      <tr *ngFor="let claim of claims; let i = index" 
                          class="hover:bg-gray-800/40 transition duration-300 opacity-0 animate-fade-in-up"
                          [ngStyle]="{'animation-delay': (i * 0.05) + 's', 'animation-fill-mode': 'forwards'}">
                        
                        <td class="py-5 px-6 font-mono text-gray-300 font-bold tracking-wider">
                           <span class="text-[#E11D48] mr-1">#</span>{{ claim.claimNumber }}
                        </td>
                        <td class="py-5 px-6 text-gray-400 font-mono text-sm">POL-{{ claim.policyId }}</td>
                        <td class="py-5 px-6 font-bold text-gray-100 flex items-center space-x-3">
                           <div class="w-8 h-8 rounded-full bg-gray-800 border border-gray-700 flex items-center justify-center text-xs text-gray-400">{{ claim.claimantName[0] }}</div>
                           <span>{{ claim.claimantName }}</span>
                        </td>
                        <td class="py-5 px-6">
                          <span class="px-3 py-1.5 rounded-md text-xs font-black tracking-wider border"
                                [ngClass]="{
                                  'bg-yellow-500/10 text-yellow-400 border-yellow-500/20 shadow-[0_0_10px_rgba(234,179,8,0.1)]': claim.status === 'PENDING',
                                  'bg-green-500/10 text-green-400 border-green-500/20 shadow-[0_0_10px_rgba(34,197,94,0.1)]': claim.status === 'APPROVED',
                                  'bg-rose-500/10 text-rose-400 border-rose-500/20 shadow-[0_0_10px_rgba(244,63,94,0.1)]': claim.status === 'REJECTED'
                                }">
                            <span class="w-1.5 h-1.5 rounded-full inline-block mr-1.5"
                                  [ngClass]="{
                                    'bg-yellow-400 animate-pulse': claim.status === 'PENDING',
                                    'bg-green-400': claim.status === 'APPROVED',
                                    'bg-rose-400': claim.status === 'REJECTED'
                                  }"></span>
                            {{ claim.status }}
                          </span>
                        </td>
                        <td class="py-5 px-6 text-right space-x-3">
                          <button *ngIf="claim.status === 'PENDING'" (click)="reviewClaim(claim.id, 'APPROVED')" class="px-4 py-2 bg-green-500/10 text-green-400 border border-green-500/30 hover:bg-green-500/20 rounded-lg text-xs font-black tracking-wider transition-all shadow-sm hover:shadow-[0_0_15px_rgba(34,197,94,0.2)]">APPROVE</button>
                          <button *ngIf="claim.status === 'PENDING'" (click)="reviewClaim(claim.id, 'REJECTED')" class="px-4 py-2 bg-rose-500/10 text-rose-400 border border-rose-500/30 hover:bg-rose-500/20 rounded-lg text-xs font-black tracking-wider transition-all shadow-sm hover:shadow-[0_0_15px_rgba(244,63,94,0.2)]">DENY</button>
                          <span *ngIf="claim.status !== 'PENDING'" class="text-gray-600 text-xs font-mono font-bold tracking-widest inline-flex items-center space-x-1">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
                            <span>LOCKED</span>
                          </span>
                        </td>
                      </tr>
                      <tr *ngIf="claims.length === 0">
                         <td colspan="5" class="py-16 text-center text-gray-500 font-medium">
                           <div class="flex flex-col items-center justify-center">
                             <svg class="w-16 h-16 text-gray-700 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
                             <p>No claims pending in the system.</p>
                           </div>
                         </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
             </div>
          </div>

          <!-- Placeholder Views for other tabs -->
          <div *ngIf="activeTab !== 'claims'" class="flex flex-col items-center justify-center h-[65vh] opacity-0 animate-fade-in-up" style="animation-delay: 0.1s; animation-fill-mode: forwards;">
             <div class="relative w-32 h-32 mb-8 group">
               <div class="absolute inset-0 bg-[#E11D48] rounded-full blur-[40px] opacity-20 group-hover:opacity-40 transition-all duration-700"></div>
               <div class="relative bg-gray-900 w-full h-full rounded-full shadow-[0_0_30px_rgba(0,0,0,0.8)] flex items-center justify-center border border-gray-700">
                 <svg class="w-12 h-12 text-gray-400 group-hover:text-white transition-colors duration-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4"></path></svg>
               </div>
             </div>
             <h2 class="text-3xl font-black text-white mb-3 tracking-widest uppercase text-transparent bg-clip-text bg-gradient-to-r from-gray-100 to-gray-500">System Offline</h2>
             <p class="text-gray-500 text-center max-w-md text-sm font-mono leading-relaxed mb-8 border border-gray-800 bg-gray-900/50 p-4 rounded-lg">
               > Module [{{ activeTab | uppercase }}] is currently undergoing core system upgrades.<br>
               > Estimated completion: Unknown.
             </p>
             <button (click)="activeTab = 'claims'" class="bg-transparent border border-gray-600 text-gray-300 hover:text-white hover:border-[#E11D48] hover:bg-[#E11D48]/10 px-8 py-3 rounded-lg font-bold font-mono text-sm tracking-widest transition-all duration-300">
               INITIALIZE CLAIMS
             </button>
          </div>

        </div>
      </main>
    </div>
  `,
  standalone: false
})
export class AdminDashboardComponent implements OnInit {
  
  claims: any[] = [];
  activeTab: string = 'claims';

  constructor(
    private http: HttpClient,
    private authService: AuthApiService,
    private router: Router
  ) {}

  ngOnInit() {
    this.fetchClaims();
  }

  fetchClaims() {
    // MOCKED DATA FOR UI PREVIEW
    this.claims = [
        { id: 101, claimNumber: 'CLM-9921-A', policyId: 2, claimantName: 'Sarah Jenkins', status: 'PENDING' },
        { id: 102, claimNumber: 'CLM-4822-B', policyId: 1, claimantName: 'Michael Rossi', status: 'APPROVED' },
        { id: 103, claimNumber: 'CLM-1093-X', policyId: 3, claimantName: 'David Chen', status: 'REJECTED' },
        { id: 104, claimNumber: 'CLM-7741-Y', policyId: 2, claimantName: 'Emma Watson', status: 'PENDING' },
        { id: 105, claimNumber: 'CLM-1192-Z', policyId: 1, claimantName: 'James Holden', status: 'PENDING' }
    ];
  }

  reviewClaim(claimId: number, status: string) {
    // Mock updating the local array
    const claim = this.claims.find(c => c.id === claimId);
    if (claim) claim.status = status;
  }

  pendingClaimsCount() {
    return this.claims.filter(c => c.status === 'PENDING').length;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  getTabClass(tabName: string) {
    if (this.activeTab === tabName) {
      return 'bg-gray-900 text-white font-bold shadow-inner border border-gray-800';
    }
    return 'text-gray-500 hover:bg-gray-900/50 hover:text-gray-300 font-medium border border-transparent';
  }
}
