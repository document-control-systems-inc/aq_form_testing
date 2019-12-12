import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { UtilitiesServices } from '../../../services/utilities.services';
import { SecurityServices } from '../../../services/security.services';
import { Properties } from '../../../services/properties';

declare var $: any;
import swal from 'sweetalert2';

@Component({
	selector: 'pantalla',
	templateUrl: './pantalla.component.html',
	styleUrls: ['./pantalla.component.css'],
	providers: [ UtilitiesServices, SecurityServices, Properties ]
})

export class PantallaComponent{
	
	public pantalla:FormGroup;
	public component:FormGroup;
	public lstPantalla;
	public idPantalla;
	public lstComponent;
	public idComponent;

	//Manejo de pantalla
	public updPantalla = 0;
	public updInfo = null;
	public updPage = {
		id:null,
		name:null
	}
	//Manejo de componente
	public updComponent = 0;
	public lstSelectedComp = null;
	public selectPage = null;
	public selectComp = null;
	public updComp = {
		id:null,
		name:null
	}

	constructor(private formBuilder: FormBuilder, private _securityServices:SecurityServices, private _utilitiesServices:UtilitiesServices){
		let that = this;
		setTimeout(function(){
			that.addStyle();
		},1);
	}

	ngOnInit(){
		this.getPage();
		this.pantalla = this.formBuilder.group({
			// To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
			name: ['', Validators.required]
		});
		this.component = this.formBuilder.group({
			// To add a validator, we must first convert the string value into an array. The first item in the array is the default value if any, then the next item in the array is the validator. Here we are adding a required validator meaning that the firstName attribute must have a value in it.
			name: ['', Validators.required],
			id: ['', Validators.required]
		});
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

	addPageSubmit() {
		if (this.pantalla.valid) {
			this.addPage(this.pantalla.value.name);
		} else {
			this.validateAllFormFields(this.pantalla);
		}
	}

	addComponentSubmit() {
		if (this.component.valid) {
			this.addComponent(this.component.value.name, this.component.value.id.id);
		} else {
			this.validateAllFormFields(this.component);
		}
	}

	getComponentSelect(){
		let that = this;
		setTimeout(function(){
			that.getComponent(that.idPantalla.id);
		},10);
	}

	addPage(name){
		this._securityServices.addPage(localStorage.getItem('token'), name).subscribe(
			result => {
			    if(result.status===0){
			    	$.notify({
                        icon: 'notifications',
                        message: 'Pantalla añadida correctamente.'
                    }, {
                        type: 'success',
                        timer: 500,
                        delay: 2000,
                        placement: {
                            from: 'top',
                            align: 'right'
                        }
                    });
                    this.getPage();  	
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.addPage(name);
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

	addComponent(name, id){
		this._securityServices.addComponent(localStorage.getItem('token'), id, name).subscribe(
			result => {
			    if(result.status===0){
			    	$.notify({
                        icon: 'notifications',
                        message: 'Componente añadido correctamente.'
                    }, {
                        type: 'success',
                        timer: 500,
                        delay: 2000,
                        placement: {
                            from: 'top',
                            align: 'right'
                        }
                    });
                    this.getPage();  	
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.addComponent(name, id);
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

	getComponent(id){
		this._securityServices.getComponent(localStorage.getItem('token'), id).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstComponent = result.exito;
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.getComponent(id);
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

	updatePage(){
		this._securityServices.updatePage(localStorage.getItem('token'), this.updPage.name, this.updPage.id).subscribe(
			result => {
			    if(result.status===0){
			    	$.notify({
                        icon: 'notifications',
                        message: 'Pantalla actualizada correctamente.'
                    }, {
                        type: 'success',
                        timer: 500,
                        delay: 2000,
                        placement: {
                            from: 'top',
                            align: 'right'
                        }
                    });
                    this.getPage();  
                    this.updPantalla = 0;
                    this.updInfo = null;	
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.updatePage();
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

	updateComponent(){
		this._securityServices.updateComponent(localStorage.getItem('token'), this.selectPage.id, this.updComp.name, this.updComp.id).subscribe(
			result => {
			    if(result.status===0){
			    	$.notify({
                        icon: 'notifications',
                        message: 'Componente modificado correctamente.'
                    }, {
                        type: 'success',
                        timer: 500,
                        delay: 2000,
                        placement: {
                            from: 'top',
                            align: 'right'
                        }
                    });
                    this.getPage();  
                    this.updComponent = 0;
                    this.selectPage = null;
                    this.selectComp = null;	
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.updateComponent();
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

	getPage(){
		this._securityServices.getPage(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstPantalla = result.exito;
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.getPage();
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

	setUpdPageInfo(){
		this.updPantalla = 1;
		this.updPage.id = this.updInfo.id;
		this.updPage.name = this.updInfo.name;
	}

	setLstComp(){
		this.lstSelectedComp = this.selectPage.components;
	}

	setUpdCompInfo(){
		this.updComponent = 1;
		this.updComp.id = this.selectComp.id;
		this.updComp.name = this.selectComp.name;
	}

}