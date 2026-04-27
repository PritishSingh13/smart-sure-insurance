import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LandingComponent } from './features/landing/landing.component';
import { CustomerDashboardComponent } from './features/dashboard/customer-dashboard.component';
import { AdminDashboardComponent } from './features/dashboard/admin-dashboard.component';
import { AuthGuard } from './auth.guard';

const routes: Routes = [
  { path: '', component: LandingComponent },
  { 
    path: 'dashboard/customer', 
    component: CustomerDashboardComponent, 
    canActivate: [AuthGuard], 
    data: { role: 'CUSTOMER' } 
  },
  { 
    path: 'dashboard/admin', 
    component: AdminDashboardComponent, 
    canActivate: [AuthGuard], 
    data: { role: 'ADMIN' } 
  },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
