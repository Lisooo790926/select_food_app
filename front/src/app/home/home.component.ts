import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, EMPTY, map, Observable, startWith, Subject } from 'rxjs';
import { DataState } from '../enums/data.state';
import { AdditionItem } from '../interface/additonal.item';
import { AppState } from '../interface/app.state';
import { Condition } from '../interface/conditions';
import { DestResponse } from '../interface/dest.response';
import { FindingHistory } from '../interface/finding.history';
import { DestapiService } from '../service/destapi.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  
  readonly DataState = DataState;
  destResponse$:Observable<AppState<DestResponse>>;
  additionalItems$:Observable<AppState<AdditionItem[]>>;
  histories$:Observable<AppState<FindingHistory[]>>;

  destResponse_cur = new BehaviorSubject<DestResponse>(null);
  additionItems = new BehaviorSubject<AdditionItem[]>(null);
  histories = new BehaviorSubject<FindingHistory[]>(null);

  constructor(private destapiService: DestapiService, private router: Router) { }

  ngOnInit(): void {
    this.getAdditionItems();
    this.getHistories();
  }

  // fetch all current resturant items
  submitCondition(conditionForm : NgForm): void {
    this.destResponse$ = this.destapiService.destResponse$(conditionForm.value as Condition)
      .pipe(
        map(result=>{
          this.destResponse_cur.next(result.data.destResponse as DestResponse);
          this.getHistories();
          return ({ dataState: DataState.LOADED_STATE, appData: this.destResponse_cur.value})
        }),
        startWith(({ dataState: DataState.LOADING_STATE, appData: this.destResponse_cur.value}))
      )
  }

  // add the current resturant by input 
  addAdditionItem(additionItemForm: NgForm): void {
    console.log(additionItemForm.value);
    this.additionalItems$ = this.destapiService.saveAdditionalItem$(additionItemForm.value as AdditionItem)
      .pipe(
        map(result=>{
          if(result.status !== "400") {
            this.additionItems.next(
              [...this.additionItems.value, result.data.saveItem]
            );
          }
          return ({ dataState: DataState.LOADED_STATE, appData: this.additionItems.value})
        }),
        startWith(({ dataState: DataState.LOADING_STATE, appData: this.additionItems.value}))
      ) 
  }

  getAdditionItems(): void {
    this.additionalItems$ = this.destapiService.getAdditionalItems$
      .pipe(
        map(result=>{
          this.additionItems.next(result.data.additionalItems)
          return ({ dataState: DataState.LOADED_STATE, appData: this.additionItems.value})
        }),
        startWith(({ dataState: DataState.LOADING_STATE, appData: this.additionItems.value}))
      ) 
  }

  deleteAdditionItem(code:string):void {
    this.additionalItems$ = this.destapiService.deleteAdditionalItem$(code)
      .pipe(
        map(result=>{
          console.log(result);
          if(result.status !== "400") {
            this.additionItems.next(
              this.additionItems.value.filter(item=> item.code !== code)
            );
          }
          return ({ dataState: DataState.LOADED_STATE, appData: this.additionItems.value})
        }),
        startWith(({ dataState: DataState.LOADING_STATE, appData: this.additionItems.value}))
      ) 
  }

  getHistories():void{
    this.histories$ = this.destapiService.getFindingHistories$
     .pipe(
       map(result=>{
         this.histories.next(result.data.histories);
         return ({ dataState: DataState.LOADED_STATE, appData: this.histories.value})
       })
     )
  }

  deleteHistory(code:string): void{
    this.histories$ = this.destapiService.deleteHistory$(code)
    .pipe(
      map(result=>{
        if(result.status !== "400") {
          this.histories.next(
            this.histories.value.filter(history=>history.code!==code)
            );
        }
        return ({ dataState: DataState.LOADED_STATE, appData: this.histories.value})
      })
    )
  }

  logout(): void {
    console.log("remove your session id");
    sessionStorage.removeItem("token");
    this.router.navigate(['/']);
  }

}
