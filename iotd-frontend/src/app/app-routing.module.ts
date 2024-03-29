import {NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {DashboardComponent} from "./components/dashboard/dashboard.component";
import {DeviceDetailsComponent} from "./components/device-details/device-details.component";
import {CreateDeviceComponent} from "./components/create-device/create-device.component";

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full'},
  { path: 'dashboard', component: DashboardComponent },
  { path: 'devices/:id', component: DeviceDetailsComponent },
  { path: 'newDevice', component: CreateDeviceComponent }
];

@NgModule({
  imports: [RouterModule.forRoot((routes))],
  exports: [RouterModule]
})
export class AppRoutingModule { }
