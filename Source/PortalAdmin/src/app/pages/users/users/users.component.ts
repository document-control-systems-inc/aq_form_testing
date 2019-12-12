import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';

import { UtilitiesServices } from '../../../services/utilities.services';
import { UsersServices } from '../../../services/users.services';
import { Properties } from '../../../services/properties';

import { Router, ActivatedRoute, Params } from '@angular/router';

@Component({
	selector: 'users',
	templateUrl: './users.component.html',
	styleUrls: ['./users.component.css'],
	providers: [ UtilitiesServices, UsersServices, Properties ]
})

export class UsersComponent{

	public add:FormGroup;
	public lstUsers;
	public suspenderId;
	public passwordId;

	constructor(private formBuilder: FormBuilder, private _usersServices:UsersServices, private _utilitiesServices:UtilitiesServices){
		let that = this;
		setTimeout(function(){
			that.addStyle();
		},1);
	}

	ngOnInit(){
		this.getUsers();
		this.add = this.formBuilder.group({
			// To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
			name: ['', Validators.required],
			lastname: ['', Validators.required],
			email: ['', Validators.required],
			uid: ['', Validators.required],
			password: ['', Validators.required],
         	confirmPassword: ['', Validators.required]
		}, {
			validator: this.matchPassword // your validation method
		});
	}

	addStyle(){
		$('.selected-container-text').css("height", "41px");	
		$('.selected-container-text').css("margin-top", "0px");
		$('.mat-tab-body-content').css("min-height", "300px");
		$('.selected-container-item').removeClass('btn-default');
		$('.caret').hide();
	}

	getUsers(){
		this._usersServices.getUsers(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstUsers = result.exito;
			    	for(let i = 0;i < this.lstUsers.length; i++){
			    		this.lstUsers[i].fullName = this.lstUsers[i].givenName + ' ' + this.lstUsers[i].lastName + ' (' + this.lstUsers[i].uid + ')';
			    	}  	
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.getUsers();
                            },1000);
                        }                        
                    });
                }
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	matchPassword(AC: AbstractControl) {
       const password = AC.get('password').value; // to get value in input tag
       const confirmPassword = AC.get('confirmPassword').value; // to get value in input tag
        if (password !== confirmPassword) {
            AC.get('confirmPassword').setErrors( {MatchPassword: true} );
        } else {
            return null;
        }
    }

	displayFieldCss(form: FormGroup, field: string) {
		return {
			'has-error': this.isFieldValid(form, field),
			'has-feedback': this.isFieldValid(form, field)
		};
	}

	isFieldValid(form: FormGroup, field: string) {
		return !form.get(field).valid && form.get(field).touched;
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

}