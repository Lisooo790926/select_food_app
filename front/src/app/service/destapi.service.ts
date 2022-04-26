import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AdditionItem } from '../interface/additonal.item';
import { Condition } from '../interface/conditions';
import { CustomResponse } from '../interface/custom.response';
import { DestResponse } from '../interface/dest.response';

@Injectable({
  providedIn: 'root'
})
export class DestapiService {

  constructor(private http: HttpClient) { }

  destResponse$ = (condition: Condition) => <Observable<CustomResponse>> 
    this.http.post<CustomResponse>(`${environment.apiUrl}/selectfood/random`, condition)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  saveAdditionalItem$ = (item: AdditionItem) => <Observable<CustomResponse>>
    this.http.post<CustomResponse>(`${environment.apiUrl}/selectfood/save/additional-item`, item)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  deleteAdditionalItem$ = (code: string) => <Observable<CustomResponse>>
    this.http.delete<CustomResponse>(`${environment.apiUrl}/selectfood/delete/additional-item/${code}`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  getAdditionalItems$ = <Observable<CustomResponse>>
    this.http.get<CustomResponse>(`${environment.apiUrl}/selectfood/all/additional-item`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  getFindingHistories$ = <Observable<CustomResponse>>
    this.http.get<CustomResponse>(`${environment.apiUrl}/selectfood/all/finding-history`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  deleteHistory$ = (code: string) => <Observable<CustomResponse>>
    this.http.delete<CustomResponse>(`${environment.apiUrl}/selectfood/delete/finding-history/${code}`)
    .pipe(
      tap(console.log),
      catchError(this.handleError)
    )

  private handleError(error: CustomResponse): Observable<never> {
    console.log(error);
    return throwError(`An error occurred - Error code : ${error.status}`);
  }
}
