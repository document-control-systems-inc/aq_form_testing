import { Component } from '@angular/core';
import { NgModel } from '@angular/forms';
import { NgClass } from '@angular/common';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { Http, Response, Headers, RequestOptions, URLSearchParams } from '@angular/http';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';
import { MatTabChangeEvent } from '@angular/material';

import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { ExplorarServices } from '../../../services/explorar.services';
import { UtilitiesServices } from '../../../services/utilities.services';
import { BusquedaServices } from '../../../services/busqueda.services';
import { Properties } from '../../../services/properties';

//imported here just for type checking. Optional
import { WebCamComponent } from 'ack-angular-webcam';

declare const $: any;
declare const contextMenu:any;

@Component({
	selector: 'captura',
	templateUrl: './captura.component.html',
	styleUrls: ['./captura.component.css'],
	providers: [ PdfViewerServices, UtilitiesServices, ExplorarServices, BusquedaServices, Properties ]
})

export class CapturaComponent{

	public webcam:WebCamComponent;//will be populated by <ack-webcam [(ref)]="webcam">
  	public base64 = null;
	public options;
	public showCamera = false;
	public continuar = false;
	public nombreFoto;
	//Clase documental seleccionada
	public claseDocumental;
	//Listado de clases documentales
	public lstClase;
	//Listado de rutas
	public lstPaths;
	//Campos pertenecientes a clase documental
	public fields;
	//properties
	public idParent;
	public idDomain = null;
	public multipleSelected;
	public documentclass;
	public properties = {

	}

	public loading = false;

	
	constructor(private translate: TranslateService, private _http:Http, private _properties:Properties, private _explorarServices: ExplorarServices, private _busquedaServices: BusquedaServices, private _pdfViewerServices:PdfViewerServices, private _utilitiesServices: UtilitiesServices, private _formBuilder: FormBuilder){
	    
	}

	ngOnInit(){
		this.getLstPath();
	}

	ngAfterViewInit(){
		let that = this;
		setTimeout(function(){
			let width = $('#camera').width();
			let height;
			if(width * 0.75 < 290) height = 290;
			else height = width * 0.75;
			that.options= {
				audio: false,
				height: height,
				width: width,
				video: true
			}		
			that.showCamera = true;
		},1200);
	}

	capturarFoto(){
		this.webcam.getBase64()
		.then( base=> {
			let date = new Date();
			this.nombreFoto = 'scan-'+Date.parse(date.toString()).toString();
			this.base64 = base;
		})
		.catch( e=>console.error(e) )
	}

	//Función utilizada para subir foto capturada
	genPostData(){
		this.loading = true;
		this.webcam.captureAsFormData({fileName: this.nombreFoto+'.png'})
		.then( formData=> {
			formData.append('token', localStorage.getItem('token'));
			formData.append('idParent', this.idParent);
			formData.append('domain', this.idDomain);
			formData.append('documentclass', this.documentclass);
			formData.append('properties', JSON.stringify(this.properties));
			this._http.post(this._properties.setDocumentByIdParent, formData)
        	.map(res => res.json()).subscribe(
        		result => {
        			this.loading = false;
					if(result.status===0){
						$.notify({
							icon: 'notifications',
							message: 'Documento agregado correctamente.'
							}, {
							type: 'success',
							timer: 500,
							delay: 2000,
							placement: {
								from: 'top',
								align: 'right'
							}
						});
						this.continuar = false;
						this.base64 = null;
					}else{
						this.loading = false;
						let that = this;
						this._utilitiesServices.showSwalError(result.status, (error, result): void => {
							if(result===true){
								setTimeout(function(){
									that.genPostData();
								},1000);
							}                        
						});
					}
				}, 
				error => {
					this.loading = false;
					var errorMessage = <any> error;
					console.log(errorMessage);
				}
        	);
		} )
		.catch( e=>console.error(e) )
	}

	onCamError(err){}

	onCamSuccess(){}

	//Función utilizada para obtener el idPath
	setPath(){
		this.idParent = this.multipleSelected.id;
	}

	//Función genérica para convertir fecha de timestamp a date con formato específico
	convertDate(timestamp){
		let date = new Date(timestamp);
		let day = date.getDate();
		let month;
		if(date.getMonth()+1 < 10){
			month = '0' + (date.getMonth()+1).toString();
		}else{
			month = (date.getMonth()+1).toString();
		}
		let year = date.getFullYear();
		let hour = date.getHours();
		let min = date.getMinutes();
		return day + '/' + month + '/' + year + ' ' + hour + ':' + min;
	} 

	continueToDocClass(button){
		this.properties = {};
		if(button===true){
			this.continuar = true;
		}
		for(let i = 0; i < this.fields.length; i++){
			let property = this.fields[i].name;
			if(property==='title'){
				this.properties[property] = this.nombreFoto;
				setTimeout(function(){
					$("#input"+i).attr("disabled", true);
				},300);
			}else{
				this.properties[property] = null;
			}
		}
		setTimeout(function(){
			$('.selected-container-text').css("background-color", "#999999");
			$('.selected-container-text').css("height","41px");
			$('.selected-container-text').css("margin-top", "0px");
			$('.mat-tab-body-content').css("min-height", "300px");
			$('.selected-container-item').removeClass('btn-default');
			$('.caret').hide();
		},30);
	}

	//Se obtiene la lista de rutas
	getLstPath(){
		this.loading = true;
		this._busquedaServices.getLstPath(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstPaths = result.exito;
			    	this.getDocument();
                }else{
                	this.loading = false;
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                            	debugger;
                                that.getLstPath();
                            },1000);
                        }                        
                    });
                }
			},
			error => {
				this.loading = false;
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	/* Clase documental y eso */
	getDocument(){
		this._pdfViewerServices.documentClass(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstClase = result.exito;
			    	for(let i = 0; i < this.lstClase.length; i++){
						if(this.lstClase[i].name==='document'){
							this.claseDocumental = this.lstClase[i];
						}
					}
					this.setDocClass();
					this.getDomain();
                }else{
                	this.loading = false;
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.getDocument();
                            },1000);
                        }                        
                    });
                }
			},
			error => {
				this.loading = false;
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	//Función utilizada para obtener idDomain
	getDomain(){
		this._explorarServices.getDomain(localStorage.getItem('token')).subscribe(
			result => {
				if(result.status===0){
					this.idDomain = result.exito[0].id;
					this.loading = false;
				}else{
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
						if(result===true){
							let that = this;
							setTimeout(function(){
								that.getDomain();
							},1000);
						}                        
					});
				}
			}, 
			error => {
				this.loading = false;
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	setDocClass(){
		this.documentclass = this.claseDocumental.id;
		this.fields = this.claseDocumental.fields;
		this.continueToDocClass(false);
	}

	//Función utilizada para indicar qué clases documentales mostrar
	showOption(id){
		if(id!=='4'&&id!=='5'&&id!=='6'){
			return true;
		}else{
			return false;
		}
	}

}