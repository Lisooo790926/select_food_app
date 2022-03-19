import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BehaviorSubject, catchError, map, Observable } from 'rxjs';
import { Condition } from './interface/conditions';
import { DestResponse } from './interface/dest.response';
import { DestapiService } from './service/destapi.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  destResponse$:Observable<string | DestResponse>;

  constructor(private destapiService: DestapiService) { }

  ngOnInit(): void {

  }

  // fetch all current resturant items
  submitCondition(conditionForm : NgForm): void {
    this.destResponse$ = this.destapiService.destResponse$(conditionForm.value as Condition)
      .pipe(
        catchError((error:string) => {
          return error;
        })
      )
  }

  // add the current resturant by input 

  // fetch all history items for only display purpose

  // submit the condition to get the result and show in the page
}
