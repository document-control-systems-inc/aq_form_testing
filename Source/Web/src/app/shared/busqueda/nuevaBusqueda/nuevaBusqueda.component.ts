import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgModel } from '@angular/forms';
import { NgClass } from '@angular/common';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';

import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { UtilitiesServices } from '../../../services/utilities.services';
import { BusquedaServices } from '../../../services/busqueda.services';
import { Properties } from '../../../services/properties';

//jquery
declare const $: any;

import swal from 'sweetalert2';

@Component({
	selector: 'nueva-busqueda',
	templateUrl: './nuevaBusqueda.component.html',
	styleUrls: ['./nuevaBusqueda.component.css'],
	providers: [ PdfViewerServices, UtilitiesServices, BusquedaServices, Properties ]
})

export class NuevaBusquedaComponent{
	//Clase documental seleccionada
	public claseDocumental;
	//Propiedades de clase documental seleccionada
	public propClaseDocumental = [];
	//Propiedades de clase documental seleccionada para visualización
	public propClaseDocumentalVisualizacion = [];
	//id de pestaña
	@Input('id') id;
	//Lista de clases documentales existentes
	@Input('lstClase') lstClase = null;
	//Lista de criterios
	@Input('lstCriteria') lstCriteria = null;
	//Lista completa de paths
	@Input('lstPaths') lstPaths = null;
	//Lista de paths seleccionados
	public lstPath:any[];
	//Objeto de criterios en string
	public criteriaString;
	//Objeto de búsqueda
	public searchData = {
		"info": {
			"name": null,
			"createdBy": "ECM",
			"createdOn": {
				"time": 1513024746070, 
				"timezone": "America/Mexico_City"
			}
		},
		"type": null,
		"searchIn": [
		],
		"includeChildren": true,
		"documentClass": [

		],
		"criteria": [
			
		],
		"visualization": [

		],
		"orderBy": null
	};
	public searchResult = null;
	@Output() execute = new EventEmitter<any[]>();

	constructor(private translate: TranslateService, private _pdfViewerServices:PdfViewerServices, private _utilitiesServices: UtilitiesServices, private _busquedaServices: BusquedaServices){

	}

	/*ngOnInit(){
		this.getDocument();
		this.getSearchType();
		this.getLstPath();
	}*/

	return(event: any[]){
		this.searchResult = null;
	}

	selected(selectedClass: any[]){
		this.claseDocumental = selectedClass;
		//Se limpia la lista de clase documental en el json general
		this.searchData.documentClass = [];
		//Se limpia la lista de campos de la clase documental
		this.propClaseDocumental = [];
		this.propClaseDocumentalVisualizacion = [];
		
		//Se añade las nuevas clases documentales a la lista
		for(let i = 0; i < this.claseDocumental.length; i++){
			this.searchData.documentClass.push(this.claseDocumental[i].id);
		}

		//Se define el tipo de búsqueda, ya sea de documento o de carpeta
		for(let i = 0; i < this.searchData.documentClass.length; i++){
			if(this.searchData.documentClass[i]==='4'){
				this.searchData.type = 'folder';
				break;
			}else{
				this.searchData.type = 'document';
			}
		}

		//Si llega a ser una clase documental diferente a las de sistema o carpeta...
		if(this.searchData.type === 'document'){
			for(let doc = 0; doc < this.lstClase.length; doc++){
				if(this.lstClase[doc].id === '5'){
					this.claseDocumental.push(this.lstClase[doc]);
					break;
				}
			}
		}

		//Se asigna la lista de campos que componen a dicha clase documental
		for(let doc = 0; doc < this.claseDocumental.length; doc++){
			for(let field = 0; field < this.claseDocumental[doc].fields.length; field++){
				this.claseDocumental[doc].fields[field].className = this.claseDocumental[doc].label;
				this.claseDocumental[doc].fields[field].documentClass = this.claseDocumental[doc].id;
				this.propClaseDocumental.push(this.claseDocumental[doc].fields[field]);
				this.propClaseDocumentalVisualizacion.push(this.claseDocumental[doc].fields[field]);
			}
		}
		this.searchData.criteria = [];
	}

	search(selectedPath: any[]){
		this.lstPath = selectedPath;
		this.searchData.searchIn = [];
		for(let i = 0; i < this.lstPath.length; i++){
			this.searchData.searchIn.push(this.lstPath[i].id);
		}
	}

	subcarpetas(subfolder: any){
		this.searchData.includeChildren = subfolder;
	}

	changeLstSelectedCriteria(lstSelectedCriteria: any[]){
		this.searchData.criteria = lstSelectedCriteria;
		this.criteriaString = JSON.stringify(lstSelectedCriteria);
	}

	orderByEvent(orderBy:any){
		this.searchData.orderBy = orderBy;
	}

	visualizationEvent(visualizationEvent:any[]){
		this.searchData.visualization = visualizationEvent;
	}

	showStorageModal(){
		let that = this;
		swal({
			title: 'Guardar búsqueda',
			html: 
			'<div class="form-inline">'+
				'<label> Nombre: </label>'+ 
					'<div class="form-group" style="margin-top: 0px;">'+
					'<input type="text" class="form-control" id="searchName" required="required">'+
				'</div>'+
			'</div>',
			showCancelButton: true,
			allowOutsideClick: false,
			allowEscapeKey: false,
			confirmButtonClass: "btn-danger",
			confirmButtonText: "Agregar"
		}).then(function(){

			// function when confirm button clicked
			that.searchData.info.name = $('#searchName').val();
			that.addSearch();
		});
	}

	addSearch(){
		this._busquedaServices.setStoredSearch(localStorage.getItem('token'), this.searchData).subscribe(
			result => {
			    if(result.status===0){
			    	$.notify({
                        icon: 'notifications',
                        message: 'Búsqueda almacenada correctamente.'
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
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.addSearch();
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

	executeSearch(){
		this._busquedaServices.executeSearch(localStorage.getItem('token'), JSON.stringify(this.searchData)).subscribe(
			result => {
			    if(result.status===0){
			    	this.searchResult = result.exito;
			    	this.searchResult.nombreTab = 'Nueva búsqueda';
			    	this.execute.emit(this.searchResult);
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.executeSearch();
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

	/*getDocument(){
		this._pdfViewerServices.documentClass(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstClase = result.exito;
                }else{
                    this._utilitiesServices.showSwalError(result.status);
                }
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	getSearchType(){
		this._busquedaServices.searchType(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstCriteria = result.exito;
                }else{
                    this._utilitiesServices.showSwalError(result.status);
                }
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	getLstPath(){
		this._busquedaServices.getLstPath(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstPaths = result.exito;
                }else{
                    this._utilitiesServices.showSwalError(result.status);
                }
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	//Función utilizada para añadir una nueva pestaña de búsqueda
	setNuevaBusqueda(){
		this.tabs.push({
			'label':'Nueva búsqueda',
			'type':'new'
		})
	}

	removeTab(i){
	    this.tabs.splice(i, 1);
	}*/

}