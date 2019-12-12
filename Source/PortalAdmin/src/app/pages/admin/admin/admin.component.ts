import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';

import { UtilitiesServices } from '../../../services/utilities.services';
import { UsersServices } from '../../../services/users.services';
import { Properties } from '../../../services/properties';

import { Router, ActivatedRoute, Params } from '@angular/router';

@Component({
	selector: 'admin',
	templateUrl: './admin.component.html',
	styleUrls: ['./admin.component.css'],
	providers: [ UtilitiesServices, UsersServices, Properties ]
})

export class AdminComponent{

	public admin:FormGroup;
	public lstUsers;
	public userId;
	public adminId;

	constructor(private formBuilder: FormBuilder, private _usersServices:UsersServices, private _utilitiesServices:UtilitiesServices){
		let that = this;
		setTimeout(function(){
			that.addStyle();
		},1);
	}

	addStyle(){
		$('.selected-container-text').css("height", "41px");	
		$('.selected-container-text').css("margin-top", "0px");
		$('.mat-tab-body-content').css("min-height", "300px");
		$('.selected-container-item').removeClass('btn-default');
		$('.caret').hide();
	}

	ngOnInit(){
		this.getUsers();
		this.admin = this.formBuilder.group({
			// To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
		});
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

	displayFieldCss(form: FormGroup, field: string) {
		return {
			'has-error': this.isFieldValid(form, field),
			'has-feedback': this.isFieldValid(form, field)
		};
	}

	isFieldValid(form: FormGroup, field: string) {
		return !form.get(field).valid && form.get(field).touched;
	}

	/*onType() {
		console.log(this.type);
		if (this.type.valid) {
			console.log('form submitted');
		} else {
			this.validateAllFormFields(this.type);
		}
	}*/

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