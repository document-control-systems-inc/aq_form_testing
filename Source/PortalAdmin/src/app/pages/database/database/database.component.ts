import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

@Component({
	selector: 'database',
	templateUrl: './database.component.html',
	styleUrls: ['./database.component.css'],
	providers: [  ]
})

export class DatabaseComponent{

	public type:FormGroup;

	constructor(private formBuilder: FormBuilder){

	}

	ngOnInit(){
		this.type = this.formBuilder.group({
			// To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
			hostname: ['', Validators.required],
			port: ['', Validators.required],
			dbName: ['', Validators.required],
			username: ['', Validators.required],
			password: ['', Validators.required],
			ValidationInterval: ['', Validators.required],
			TimeBetweenEvictionRuns: ['', Validators.required],
			MaxActive: ['', Validators.required],
			InitialSize: ['', Validators.required],
			MaxWait: ['', Validators.required],
			RemoveAbandonedTimeout: ['', Validators.required],
			MinEvictableIdleTime: ['', Validators.required],
			MinIdle: ['', Validators.required]
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

}