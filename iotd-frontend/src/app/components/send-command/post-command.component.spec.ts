import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PostCommandComponent} from './post-command.component';

describe('SendCommandComponent', () => {
  let component: PostCommandComponent;
  let fixture: ComponentFixture<PostCommandComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PostCommandComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PostCommandComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
