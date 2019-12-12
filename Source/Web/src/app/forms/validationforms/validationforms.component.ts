import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';
import { PasswordValidation } from './password-validator.component';

declare interface ValidatorFn {
    (c: AbstractControl): {
        [key: string]: any;
    };
}
declare interface User {
    text?: string; // required, must be 5-8 characters
    email?: string; // required, must be valid email format
    password?: string; // required, value must be equal to confirm password.
    confirmPassword?: string; // required, value must be equal to password.
    number?: number; // required, value must be equal to password.
    url?: string;
    idSource?: string;
    idDestination?: string;
    optionsCheckboxes?: boolean;
}

@Component({
    selector: 'app-validationforms-cmp',
    templateUrl: 'validationforms.component.html'
})

export class ValidationFormsComponent implements OnInit {
    // public user: User;
    public typeValidation: User;
    register : FormGroup;
    login : FormGroup;
    type : FormGroup;

    constructor(private formBuilder: FormBuilder) {}
 // pattern=" [a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?"

 isFieldValid(form: FormGroup, field: string) {
   return !form.get(field).valid && form.get(field).touched;
 }

 displayFieldCss(form: FormGroup, field: string) {
   return {
     'has-error': this.isFieldValid(form, field),
     'has-feedback': this.isFieldValid(form, field)
   };
 }

 onRegister() {
   console.log(this.register);
   if (this.register.valid) {
     console.log('form submitted');
   } else {
     this.validateAllFormFields(this.register);
   }
 }
 onLogin() {
   console.log(this.login);
   if (this.login.valid) {
     console.log('form submitted');
   } else {
     this.validateAllFormFields(this.login);
   }
 }
 onType() {
   console.log(this.type);
   if (this.type.valid) {
     console.log('form submitted');
   } else {
     this.validateAllFormFields(this.type);
   }
 }
 validateAllFormFields(formGroup: FormGroup) {
   Object.keys(formGroup.controls).forEach(field => {
     console.log(field);
     const control = formGroup.get(field);
     if (control instanceof FormControl) {
       control.markAsTouched({ onlySelf: true });
     } else if (control instanceof FormGroup) {
       this.validateAllFormFields(control);
     }
   });
 }
  ngOnInit() {
      this.register = this.formBuilder.group({
        // To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
        email: [null, [Validators.required,Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$")]],
        // We can use more than one validator per field. If we want to use more than one validator we have to wrap our array of validators with a Validators.compose function. Here we are using a required, minimum length and maximum length validator.
        optionsCheckboxes: ['', Validators.required],
        password: ['', Validators.required],
        confirmPassword: ['', Validators.required],
       }, {
         validator: PasswordValidation.MatchPassword // your validation method
     });
     this.login = this.formBuilder.group({
       // To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
       email: [null, [Validators.required,Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$")]],
       // We can use more than one validator per field. If we want to use more than one validator we have to wrap our array of validators with a Validators.compose function. Here we are using a required, minimum length and maximum length validator.
       password: ['', Validators.required]
    });
       this.type = this.formBuilder.group({
         // To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
         text: [null, Validators.required],
         email: [null, [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$")]],
         number: [null , Validators.required],
         url: [null, Validators.required],
         // We can use more than one validator per field. If we want to use more than one validator we have to wrap our array of validators with a Validators.compose function. Here we are using a required, minimum length and maximum length validator.
         password: ['', Validators.required],
         confirmPassword: ['', Validators.required],
        }, {
          validator: PasswordValidation.MatchPassword // your validation method
      });
  }
}
