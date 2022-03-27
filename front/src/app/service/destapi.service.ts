import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { AdditionItem } from '../interface/additonal.item';
import { Condition } from '../interface/conditions';
import { CustomResponse } from '../interface/custom.response';
import { DestResponse } from '../interface/dest.response';

@Injectable({
  providedIn: 'root'
})
export class DestapiService {

  // todo change to use environment variables
  private readonly apiUrl = 'http://localhost:8083';

  constructor(private http: HttpClient) { }

  destResponse$ = (condition: Condition) => <Observable<CustomResponse>>
    this.http.post<CustomResponse>(`${this.apiUrl}/selectfood/random`, condition)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  saveAdditionalItem$ = (item: AdditionItem) => <Observable<CustomResponse>>
    this.http.post<CustomResponse>(`${this.apiUrl}/selectfood/save/additional-item`, item)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  deleteAdditionalItem$ = (code: string) => <Observable<CustomResponse>>
    this.http.delete<CustomResponse>(`${this.apiUrl}/selectfood/delete/additional-item/${code}`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  getAdditionalItems$ = <Observable<CustomResponse>>
    this.http.get<CustomResponse>(`${this.apiUrl}/selectfood/all/additional-item`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  getFindingHistories$ = <Observable<CustomResponse>>
    this.http.get<CustomResponse>(`${this.apiUrl}/selectfood/all/finding-history`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  private handleError(error: CustomResponse): Observable<never> {
    console.log(error);
    return throwError(`An error occurred - Error code : ${error.status}`);
  }
}
