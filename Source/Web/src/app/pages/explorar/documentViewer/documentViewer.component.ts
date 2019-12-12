import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgModel } from '@angular/forms';
import { NgClass } from '@angular/common';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';
import { ExplorarServices } from '../../../services/explorar.services';
import { UtilitiesServices } from '../../../services/utilities.services';
import { Properties } from '../../../services/properties';

@Component({
	selector: 'documentViewer',
	templateUrl: './documentViewer.component.html',
	styleUrls: ['./documentViewer.component.css'],
	providers: [ ExplorarServices, UtilitiesServices, Properties ]
})

export class DocumentViewerComponent{
	//Nombre de pantalla actual
	public componentName:string;
	//Boolean utilizado para alternar entre pantalla de visor y editor de pdf
	public isEditor:boolean = false;
	//Declaración de idDocumento
	@Input('idDoc') idDocumento;
	//Declaración de lista de documentos
	@Input('lstDoc') lstDocumentos;
	//Información de propiedades de documento
	public metadata = null;
	//Información de propiedades de sistema
	public systemProperties = null;
	//Información de historial de versiones
	public historial = null; 
	//Lugar del documento actual
	public indexDoc;
	//evento de regresar a explorar
	@Output() regresar = new EventEmitter<any>();
	//evento para definir nombre de tabs
	@Output() tabs = new EventEmitter<any>();
	//Nombre de documento mostrado
	public docTitle;
	//Bandera de visor
	public isVisor = false;
	//Variable de manejo de componente de loading
  	public loading = false; 
	
	constructor(private translate: TranslateService, private _explorarServices: ExplorarServices, private _utilitiesServices: UtilitiesServices){
		let that = this;
		setTimeout(function(){
			for(let i = 0; i < that.lstDocumentos.length; i++){
				if(that.lstDocumentos[i]===that.idDocumento){
					that.indexDoc = i;
					break;
				}
			}		
		},50);
	}

	ngOnInit(){
		let that = this;
		setTimeout(function(){
			that.getDocMetadata(that.idDocumento);		
		},500);
	}

	//Función utilizada para obtener los metadatos de un documento
	getDocMetadata(id){
		this.loading = true;
		this._explorarServices.getDocMetadata(localStorage.getItem('token'), id).subscribe(
			result => {
				if(result.status===0){
					this.metadata = result.exito;
					this.docTitle = this.metadata[0].properties.title;
					if(this.lstDocumentos.length===0) this.tabs.emit(this.docTitle);
					this.getSystemProperties(this.idDocumento);
				}else{
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.getDocMetadata(id);
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

	//Función utilizada para obtener las propiedades de sistema de un documento
	getSystemProperties(id){
		this._explorarServices.getSystemProperties(localStorage.getItem('token'), id).subscribe(
			result => {
				if(result.status===0){
					this.systemProperties = result.exito.properties;
					this.getContentVersion(this.idDocumento);
				}else{
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.getSystemProperties(id);
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

	//Función utilizada para obtener el historial de versiones de un documento
	getContentVersion(id){
		this._explorarServices.getHistorial(localStorage.getItem('token'), id).subscribe(
			result => {
				if(result.status===0){
					this.historial = result.exito;
					this.loading = false;
				}else{
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        this.loading = false;
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.getContentVersion(id);
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

	showEditor(){
		this.isEditor = true;
		$("#preview").css({ 'display': "block" });
		//Se añade el responsivo en el visor de tiff
		/*setTimeout(function(){
			let canvasHeight =  $('#canvas1Visor').height();
			$('#previewVisor').css('max-height', canvasHeight);
		},200);*/
	}

	hideEditor(){
		this.isEditor = false;
		//Se añade el responsivo en el visor de tiff
		/*setTimeout(function(){
			let canvasHeight =  $('#canvas1Visor').height();
			$('#previewVisor').css('max-height', canvasHeight);
		},200);*/
	}

	showPreview(){
		this.isVisor = true;
		$("#preview").css({ 'display': "block" });
		this.hideEditor();
		//Se añade el responsivo en el visor de tiff
		/*setTimeout(function(){
			let canvasHeight =  $('#canvas1Visor').height();
			$('#previewVisor').css('max-height', canvasHeight);
		},200);*/
	}

	hidePreview(){
		this.isVisor = false;
		this.hideEditor();
		//Se añade el responsivo en el visor de tiff
		/*setTimeout(function(){
			let canvasHeight =  $('#canvas1Visor').height();
			$('#previewVisor').css('max-height', canvasHeight);
		},200);*/
	}

	regresarAExplorar(){
		this.regresar.emit('explorar');
	}

	prevDoc(){
		this.idDocumento = this.lstDocumentos[this.indexDoc-1];
		this.getDocMetadata(this.idDocumento);
		this.indexDoc--;
	}

	nextDoc(){
		this.idDocumento = this.lstDocumentos[this.indexDoc+1];
		this.getDocMetadata(this.idDocumento);
		this.indexDoc++;
	}

}