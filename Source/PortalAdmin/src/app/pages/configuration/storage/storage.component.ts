import { Component } from '@angular/core';

import { Router, ActivatedRoute, Params } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';

declare var $: any;

@Component({
	selector: 'storage',
	templateUrl: './storage.component.html',
	styleUrls: ['./storage.component.css'],
	providers: [  ]
})

export class StorageComponent{

	public path:FormGroup;
	public add:FormGroup;
	public update:FormGroup;

	constructor(private formBuilder: FormBuilder){
		let that = this;
		setTimeout(function(){
			that.addStyle();
		},1);
		this.path = this.formBuilder.group({
			// To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
			storagePath: ['', Validators.required]
		});
		this.add = this.formBuilder.group({
			// To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
			domain: ['', Validators.required],
			name: ['', Validators.required],
			path: ['', Validators.required],
			status: ['', Validators.required]
		});
		this.update = this.formBuilder.group({
			// To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
			domain: ['', Validators.required],
			name: ['', Validators.required],
			path: ['', Validators.required],
			status: ['', Validators.required]
		});
	}

	ngOnInit(){
			
	}

	addStyle(){
		$('.selected-container-text').css("height", "41px");	
		$('.selected-container-text').css("margin-top", "0px");
		$('.mat-tab-body-content').css("min-height", "300px");
		$('.selected-container-item').removeClass('btn-default');
		$('.caret').hide();
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