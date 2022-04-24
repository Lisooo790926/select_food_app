import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, tap, throwError } from 'rxjs';
import { CustomResponse } from '../interface/custom.response';
import { User } from '../interface/user';
import { UserResponse } from '../interface/user.response';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // todo change to use environment variables
  private readonly apiUrl = 'http://localhost:8083';

  constructor(private http: HttpClient) { }

  userLoginResponse$ = (user: User) => <Observable<CustomResponse>>
  this.http.post<CustomResponse>(`${this.apiUrl}/api/auth/signin`, user)
    .pipe(
      map(result=> {
        if(result && result.status === "OK") {
          let tokendata = result.data["token"] as UserResponse;
          let token = tokendata.type + " " + tokendata.token;
          return {status : "OK", data: {apitoken : token}};
        }
        console.log(result, "Bad_requst")
        return {status : "BAD_REQUEST", message: "login in failed"};
      }),
      catchError(this.handleError)
    )

  userSignupResponse$ = (user: User) => <Observable<CustomResponse>>
  this.http.post<CustomResponse>(`${this.apiUrl}/api/auth/signup`, user)
    .pipe(
      tap(console.log),
      catchError(this.handleError)
    )

  isUserLoggedIn = () => {
    let token = sessionStorage.getItem('token')
    return !(token === null)
  } 

  private handleError(error: CustomResponse): Observable<never> {
    console.log(error);
    return throwError(`An error occurred - Error code : ${error.status}`);
  }  
}
