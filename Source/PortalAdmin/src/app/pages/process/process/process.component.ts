import { Component, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';

import { UtilitiesServices } from '../../../services/utilities.services';
import { UsersServices } from '../../../services/users.services';
import { Properties } from '../../../services/properties';

import { Router, ActivatedRoute, Params } from '@angular/router';
import { DropzoneComponent, DropzoneDirective, DropzoneConfigInterface } from 'ngx-dropzone-wrapper';

@Component({
	selector: 'process',
	templateUrl: './process.component.html',
	styleUrls: ['./process.component.css'],
	providers: [ UtilitiesServices, UsersServices, Properties ]
})

export class ProcessComponent{

	@ViewChild('add') addRef: DropzoneComponent;
	@ViewChild('update') updateRef: DropzoneComponent;

	public file = null;
	public newProcess = {
		'name':null,
		'file':null
	}
	public updateProcess = {
		'name':null,
		'file':null
	}
	public config: DropzoneConfigInterface = {
		clickable: true,
		maxFiles:1
	};

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

	getAddFile(e){
		this.newProcess.file = e[0];
	}

	resetAddFiles(){
		this.addRef.directiveRef.reset();
		this.newProcess.file = null;
	}

	getUpdateFile(e){
		this.updateProcess.file = e[0];
	}

	resetUpdateFiles(){
		this.updateRef.directiveRef.reset();
		this.updateProcess.file = null;
	}

	ngOnInit(){

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