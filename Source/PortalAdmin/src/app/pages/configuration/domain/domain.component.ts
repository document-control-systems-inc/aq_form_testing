import { Component } from '@angular/core';

import { Router, ActivatedRoute, Params } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';

import { UtilitiesServices } from '../../../services/utilities.services';
import { DomainServices } from '../../../services/domain.services';
import { Properties } from '../../../services/properties';

declare var $: any;

@Component({
	selector: 'domain-config',
	templateUrl: './domain.component.html',
	styleUrls: ['./domain.component.css'],
	providers: [ UtilitiesServices, DomainServices, Properties ]
})

export class DomainConfigComponent{

	public domainForm:FormGroup;
	public subdomain:FormGroup;
	public lstDomain = null;

	constructor(private formBuilder: FormBuilder, private _domainServices:DomainServices, private _utilitiesServices:UtilitiesServices){
		let that = this;
		setTimeout(function(){
			that.addStyle();
		},1);
		this.domainForm = this.formBuilder.group({
			// To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
			domain: ['', Validators.required]
		});
		this.subdomain = this.formBuilder.group({
			// To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
			id: ['', Validators.required],
			domain: ['', Validators.required]
		});
	}

	ngOnInit(){
		this.getDomain();	
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

	getDomain(){
		this._domainServices.getDomain(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstDomain = result.exito;
			    	for(let i = 0;i < this.lstDomain.length; i++){
			    		this.lstDomain[i].domainName = this.lstDomain[i].properties.name;
			    	} 
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        /*if(result===true){
                        	let that = this;
                            setTimeout(function(){
                            	debugger;
                                that.getDocument();
                            },1000);
                        }*/                      
                    });
                }
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	setDomain() {
		if (this.domainForm.valid) {
			this._domainServices.setDomain(this.domainForm.value.domain, localStorage.getItem('token')).subscribe(
				result => {
				    if(result.status===0){
				    	$.notify({
	                        icon: 'notifications',
	                        message: 'Dominio agregado correctamente.'
	                    }, {
	                        type: 'success',
	                        timer: 500,
	                        delay: 2000,
	                        placement: {
	                            from: 'top',
	                            align: 'right'
	                        }
	                    });
	                }else{
	                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
	                        /*if(result===true){
	                        	let that = this;
	                            setTimeout(function(){
	                            	debugger;
	                                that.getDocument();
	                            },1000);
	                        }*/                      
	                    });
	                }
				},
				error => {
					var errorMessage = <any> error;
					console.log(errorMessage);
				}
			);
		} else {
			this.validateAllFormFields(this.domainForm);
		}
	}

	setSubdomain() {
		if (this.subdomain.valid) {
			this._domainServices.setSubdomain(this.subdomain.value.domain, localStorage.getItem('token'), this.subdomain.value.id).subscribe(
				result => {
				    if(result.status===0){
				    	$.notify({
	                        icon: 'notifications',
	                        message: 'Subdominio agregado correctamente.'
	                    }, {
	                        type: 'success',
	                        timer: 500,
	                        delay: 2000,
	                        placement: {
	                            from: 'top',
	                            align: 'right'
	                        }
	                    });
	                }else{
	                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
	                        /*if(result===true){
	                        	let that = this;
	                            setTimeout(function(){
	                            	debugger;
	                                that.getDocument();
	                            },1000);
	                        }*/                      
	                    });
	                }
				},
				error => {
					var errorMessage = <any> error;
					console.log(errorMessage);
				}
			);
		} else {
			this.validateAllFormFields(this.subdomain);
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