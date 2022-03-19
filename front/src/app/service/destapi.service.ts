import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { Condition } from '../interface/conditions';
import { DestResponse } from '../interface/dest.response';

@Injectable({
  providedIn: 'root'
})
export class DestapiService {

  // todo change to use environment variables
  private readonly apiUrl = 'http://localhost:8083';

  constructor(private http: HttpClient) { }

  // fetch data from backend 

  destResponse$ = (condition : Condition) => <Observable<DestResponse>>
    this.http.post<DestResponse>(`${this.apiUrl}/selectfood/random`, condition)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      )

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.log(error);
    return throwError(`An error occurred - Error code : ${error.status}`);
  }
}
