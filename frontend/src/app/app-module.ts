import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing-module';
import { AuthInterceptor } from './features/auth/auth.interceptor';
import { App } from './app';
import { LandingComponent } from './features/landing/landing.component';
import { CustomerDashboardComponent } from './features/dashboard/customer-dashboard.component';
import { AdminDashboardComponent } from './features/dashboard/admin-dashboard.component';

@NgModule({
  declarations: [
    App,
    LandingComponent,
    CustomerDashboardComponent,
    AdminDashboardComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [App]
})
export class AppModule { }
