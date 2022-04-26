import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BehaviorSubject, map } from 'rxjs';
import { User } from '../interface/user';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  constructor(private authService : AuthService, private modalService: NgbModal) { }

  private closeResult: string;
  private loading = new BehaviorSubject<boolean>(false);
  private errorMessage = new BehaviorSubject<string>("");

  loading$ = this.loading.asObservable();
  errorMessage$ = this.errorMessage.asObservable();

  ngOnInit(): void {
  }

  open(content:any) {
    console.log(content);
    this.modalService.open(content).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    }).then(result => console.log(this.closeResult));
  }

  getDismissReason(reason: any) {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return  `with: ${reason}`;
    }
  }

  registerUser(userForm: NgForm) {
    let user = userForm.value as User;
    
    if(user.username == '' || user.email == '' || user.password == '') {
      console.log("Not available");
      this.errorMessage.next("Invalid property")
      return;
    }
    this.loading.next(true);
    this.authService.userSignupResponse$(user)
      .pipe(
        map(result => {
          console.log(result);
          if(result.status === "OK") {
            this.errorMessage.next("");
            this.modalService.dismissAll();
          } else {
            this.errorMessage.next(result.message);
          }
          this.loading.next(false);
        })
      ).subscribe();
  }

}
