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
import {PostCommandComponent} from './components/send-command/post-command.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatCardModule} from "@angular/material/card";
import {MatDialogModule} from "@angular/material/dialog";
import {MatInputModule} from "@angular/material/input";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {EditParametersComponent} from './components/edit-parameters/edit-parameters.component';
import {ParameterViewComponent} from './components/parameter-view/parameter-view.component';
import {ChartModule} from "angular2-highcharts";
import {HighchartsStatic} from "angular2-highcharts/dist/HighchartsService";

declare var require: any;
export function highchartsFactory() {
  return require('highcharts');
}

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    DeviceDetailsComponent,
    CreateDeviceComponent,
    PostCommandComponent,
    EditParametersComponent,
    ParameterViewComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDialogModule,
    MatInputModule,
    MatTooltipModule,
    MatSnackBarModule,
    ChartModule.forRoot(require('highcharts'))
  ],
  providers: [{
    provide: HighchartsStatic,
    useFactory: highchartsFactory
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
