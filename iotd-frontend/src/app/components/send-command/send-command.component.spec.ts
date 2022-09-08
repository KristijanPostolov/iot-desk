import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SendCommandComponent} from './send-command.component';

describe('SendCommandComponent', () => {
  let component: SendCommandComponent;
  let fixture: ComponentFixture<SendCommandComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SendCommandComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SendCommandComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
