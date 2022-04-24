import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BehaviorSubject, catchError, map, Observable, Subject, throwError } from 'rxjs';
import { CustomResponse } from './interface/custom.response';
import { User } from './interface/user';
import { AuthService } from './service/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  
  constructor(private authService: AuthService) { }

  isSignUp$:Observable<boolean>;

  public isLogin = new BehaviorSubject<boolean>(false);
  public isLoginFail = new BehaviorSubject<boolean>(false);

  ngOnInit(): void {
    this.isLogin.next(this.authService.isUserLoggedIn())
  }

  login(userForm:NgForm): void {
    this.authService.userLoginResponse$(userForm.value as User)
      .pipe(
        map(result=>{
          console.log(result);
          let isSuccess = result && result.status === "OK"
          if(isSuccess) {
            sessionStorage.setItem("token", result.data.apitoken);
          } 
          this.isLoginFail.next(!isSuccess)
          this.isLogin.next(this.authService.isUserLoggedIn())
          return isSuccess;
        }),
        catchError(this.handleError)
      ).subscribe()
  }

  signup(user:User): void {
    this.isSignUp$ = this.authService.userSignupResponse$(user) 
      .pipe(
        map(result=>{
          return result && result.status === "OK";
        })
      )
  }

  checkLogin(isLogin:boolean): void {
    let token = sessionStorage.getItem("token");
    if(!token && !isLogin){
      console.log("log out sucessfully")
      this.isLogin.next(isLogin);
    }
  }

  private handleError(error: CustomResponse): Observable<never> {
    console.log(error);
    return throwError(`An error occurred - Error code : ${error.status}`);
  } 
}

