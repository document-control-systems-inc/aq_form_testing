import { Component } from '@angular/core';

import { Router, ActivatedRoute, Params } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';

import { UtilitiesServices } from '../../../services/utilities.services';
import { SecurityServices } from '../../../services/security.services';
import { Properties } from '../../../services/properties';

declare var $: any;

@Component({
	selector: 'permisos',
	templateUrl: './permisos.component.html',
	styleUrls: ['./permisos.component.css'],
	providers: [ UtilitiesServices, SecurityServices, Properties ]
})

export class PermisosComponent{

	public lstPantalla;
	public lstProfile;
	public lstUsers;
	public objUser;
	public objProfile;
	public idUser;
	public idProfile;
	public page;
	public profileName = '';
	public elements = [];
	public lstUsersGroups = [
		{
			id: 1,
			name: 'Grupo de ejemplo (Grupo)'
		}
	]

	constructor(private formBuilder: FormBuilder, private _securityServices:SecurityServices, private _utilitiesServices:UtilitiesServices){
		let that = this;
		setTimeout(function(){
			that.addStyle();
		},1);
	}

	ngOnInit(){
		this.getPage();
	}

	addStyle(){
		$('.selected-container-text').css("height", "41px");	
		$('.selected-container-text').css("margin-top", "0px");
		$('.mat-tab-body-content').css("min-height", "300px");
		$('.selected-container-item').removeClass('btn-default');
		$('.caret').hide();
	}

	setIdUser(){
		this.idUser = this.objUser.uid;
	}

	setIdProfile(){
		this.idProfile = this.objProfile.id;
	}

	addProfile(){
		this._securityServices.addProfile(localStorage.getItem('token'), this.profileName, this.elements).subscribe(
			result => {
			    if(result.status===0){
			    	$.notify({
                        icon: 'notifications',
                        message: 'Perfil añadido correctamente.'
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
                                that.addProfile();
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

	getUser(){
		this._securityServices.getUser(localStorage.getItem('token')).subscribe(
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
                                that.getUser();
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

	getProfile(){
		this._securityServices.getProfile(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstProfile = result.exito;
			    	this.getUser();
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.getProfile();
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
			    	this.getProfile();
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

	setElements(){
		//Se limpia el objeto elements
		this.elements = [];
		//Ciclo de objetos
		for(let iteradorPage = 0; iteradorPage < this.page.length; iteradorPage++){

			//Es componente
			if(this.page[iteradorPage].components === undefined && this.page[iteradorPage].components !== []){
				//Se define una variable para almacenar el id de page
				let idPage;
				//Se iteran todas las page existentes
				for(let iteradorPantalla = 0; iteradorPantalla < this.lstPantalla.length; iteradorPantalla++){
					//Se iteran los componentes de esa page
					for(let iteradorComp = 0; iteradorComp < this.lstPantalla[iteradorPantalla].components.length; iteradorComp++){
						//Se compara el id del componente seleccionado con cada uno de los componentes
						if(this.lstPantalla[iteradorPantalla].components[iteradorComp].id === this.page[iteradorPage].id){
							//Se define el idPage para poder armar el elemento nuevo
							idPage = this.lstPantalla[iteradorPantalla].id;
							break;
						}
					}
				}
				//Variable utilizada para validar si ya existe el componente en elements
				let existe = false;
				//Se verifica si no existe ya el elemento en elements
				for(let iteradorElements = 0; iteradorElements < this.elements.length; iteradorElements++){
					//Si existe
					if(this.elements[iteradorElements].pageId === idPage){
						this.elements[iteradorElements].componentsId.push(this.page[iteradorPage].id);
						existe = true;
						break;
					}
				}
				//No existe
				if(existe === false){
					this.elements.push({
						"pageId":idPage,
						"componentsId":[this.page[iteradorPage].id]
					});
				}
			}

			//Es página
			else{
				//Página con componentes
				if(this.page[iteradorPage].components.length > 0){
					this.elements.push({
						"pageId":this.page[iteradorPage].id,
						"componentsId":[]
					});
					for(let iteradorComp = 0; iteradorComp < this.page[iteradorPage].components.length; iteradorComp++){
						this.elements[this.elements.length-1].componentsId.push(this.page[iteradorPage].components[iteradorComp].id);
					}
				}
				//Página sin componentes
				else{
					this.elements.push({
						"pageId":this.page[iteradorPage].id,
						"componentsId":[]
					});
				}
			}
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