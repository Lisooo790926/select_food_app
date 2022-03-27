import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BehaviorSubject, catchError, EMPTY, map, Observable, startWith, Subject } from 'rxjs';
import { DataState } from './enums/data.state';
import { AdditionItem } from './interface/additonal.item';
import { AppState } from './interface/app.state';
import { Condition } from './interface/conditions';
import { CustomResponse } from './interface/custom.response';
import { DestResponse } from './interface/dest.response';
import { DestapiService } from './service/destapi.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  readonly DataState = DataState;
  destResponse$:Observable<AppState<DestResponse>>;
  additionalItem$:Observable<AppState<AdditionItem>>;
  additionalItems$:Observable<AppState<AdditionItem[]>>;

  destResponse_cur = new BehaviorSubject<DestResponse>(null);
  additionItems = new BehaviorSubject<AdditionItem[]>(null);

  constructor(private destapiService: DestapiService) { }

  ngOnInit(): void {
    this.getAdditionItems();
  }

  // fetch all current resturant items
  submitCondition(conditionForm : NgForm): void {
    this.destResponse$ = this.destapiService.destResponse$(conditionForm.value as Condition)
      .pipe(
        map(result=>{
          this.destResponse_cur.next(result.data.destResponse as DestResponse);
          return ({ dataState: DataState.LOADED_STATE, appData: this.destResponse_cur.value})
        }),
        startWith(({ dataState: DataState.LOADING_STATE, appData: this.destResponse_cur.value}))
      )
  }

  // add the current resturant by input 
  addAdditionItem(additionalForm: NgForm): void {
    this.additionalItem$ = this.destapiService.saveAdditionalItem$(additionalForm.value as AdditionItem)
      .pipe(
        map(result=>{
          return ({ dataState: DataState.LOADED_STATE, appData: result.data.saveItem})
        }),
        startWith(({ dataState: DataState.LOADING_STATE}))
      ) 
  }

  getAdditionItems(): void {
    this.additionalItems$ = this.destapiService.getAdditionalItems$
      .pipe(
        map(result=>{
          return ({ dataState: DataState.LOADED_STATE, appData: result.data.additionalItems})
        }),
        startWith(({ dataState: DataState.LOADING_STATE}))
      ) 
  }



  // fetch all history items for only display purpose

}
