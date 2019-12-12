import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

@Component({
	selector: 'session',
	templateUrl: './session.component.html',
	styleUrls: ['./session.component.css'],
	providers: [  ]
})

export class SessionComponent{

	public form:FormGroup;

	constructor(private formBuilder: FormBuilder){

	}

	ngOnInit(){
		this.form = this.formBuilder.group({
			// To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
			minutes: ['', Validators.required],
			maxUsers: ['', Validators.required],
			maxConcurrent: ['', Validators.required]
		});
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

	onType() {
		console.log(this.form);
		if (this.form.valid) {
			console.log('form submitted');
		} else {
			this.validateAllFormFields(this.form);
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

}