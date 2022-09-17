import {Component, Input, OnInit} from '@angular/core';
import {Parameter} from "../../models/parameter";
import {ParameterSnapshot} from "../../models/parameter-snapshot";

@Component({
  selector: 'app-parameter-view',
  templateUrl: './parameter-view.component.html',
  styleUrls: ['./parameter-view.component.css']
})
export class ParameterViewComponent implements OnInit {

  @Input()
  parameter: Parameter | undefined;

  @Input()
  parameterSnapshots: ParameterSnapshot[] | undefined;

  constructor() { }

  ngOnInit(): void {
  }

}
