import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {AppRoutingModule} from './app-routing.module';
import {HttpClientModule} from "@angular/common/http";
import {DeviceDetailsComponent} from './components/device-details/device-details.component';
import {CommonModule} from "@angular/common";
import {CreateDeviceComponent} from './components/create-device/create-device.component';
import {FormsModule} from "@angular/forms";
import {SendCommandComponent} from './components/send-command/send-command.component';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    DeviceDetailsComponent,
    CreateDeviceComponent,
    SendCommandComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
