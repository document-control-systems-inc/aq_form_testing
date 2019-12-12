import { Component, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { ExplorarServices } from '../../../services/explorar.services';
import { UtilitiesServices } from '../../../services/utilities.services';
import { Properties } from '../../../services/properties';

import * as $ from 'jquery';
import swal from 'sweetalert2';

import { TranslateService } from '@ngx-translate/core';

@Component({
	selector: 'folder-viewer',
	templateUrl: './folderViewer.component.html',
	styleUrls: ['./folderViewer.component.css'],
	providers: [ UtilitiesServices, Properties, ExplorarServices ]
})

export class FolderViewerComponent{

	//Id de documento
	@Input('id') idDocument;
	//Lista de clases documentales
	@Input('class') lstClase;
	//Evento de cambio de carpeta
	@Output() change = new EventEmitter<any[]>();
	@Output() thumbnail = new EventEmitter<any[]>();
	@Output() viewer = new EventEmitter<any[]>();
	//Listado resultados
	public dataTable;
	//Nombre de carpeta actual
	public folderName;
	//Paginado
	public pages = [];
	public selectedPage = 1;
	//Filtrado
	public filter;
	//Elementos de modal de propiedades
	public metadata;
	public systemProperties;
	public historial;
	//Vista de tabla
	public view = 'table';
	//Variable utilizada para almacenar la lista de id del folder padre
	public lstParentFolder;
	//Variable utilizada para almacenar el tamaño de la lista de id del folder padre
	public lstParentFolderSize;

	constructor(private translate: TranslateService, private _utilitiesServices: UtilitiesServices, private _explorarServices: ExplorarServices){	
		
	}

	ngOnInit(){
		this.getFolderContent(this.idDocument);
		let that = this;
		setTimeout(function(){
			that.folderName = that.systemProperties.name;
		},500);		
	}

	ngOnChanges(changes: SimpleChanges){
		if(changes.idDocument.currentValue!==undefined && changes.idDocument.firstChange===false){
			this.getParentFolder(this.idDocument);
			this.getFolderContent(this.idDocument);
		}
	}

  	//Función para ir a página anterior
	prevPage(){
		if(this.selectedPage>1){
			this.selectedPage--;
			if(this.view!=='table'){
				this.getLstThumbnails(this.selectedPage);
			}  
		}
	}

	//Función para ir a la página siguiente
	nextPage(){
		if(this.selectedPage<this.pages.length){
			this.selectedPage++;
			if(this.view!=='table'){
				this.getLstThumbnails(this.selectedPage);
			}
		}
	}

	goToViewer(name){
		this.viewer.emit(name);
	}

	getLstThumbnails(page){
		let thumbnailIndex;
		if((this.selectedPage-1)*12 < this.dataTable.dataRows.length){
			thumbnailIndex = this.dataTable.dataRows.length;
		}else{
			thumbnailIndex = this.selectedPage*12;
		}
		for(let i = (this.selectedPage - 1) * 12; i < thumbnailIndex; i++){
			this.getThumbnail(i);
		}
		this.thumbnail.emit(this.dataTable);
		this.view = 'thumbnail';
	}

	//Función utilizada para obtener la información del árbol
	getThumbnail(index){
		let id = this.dataTable.dataRows[index][5];
		this._explorarServices.getThumbnail(localStorage.getItem('token'), id).subscribe(
			result => {
				let blob = new Blob([result], {type:'image/png'});
				var reader = new FileReader();
				reader.readAsDataURL(blob); 
				let that = this;
				reader.onloadend = function() {
					that.dataTable.dataRows[index].push(reader.result);        
				}
			},
			error => {
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
					let that = this;
					setTimeout(function(){
						that.getSystemProperties(id);
					},50);
				}else{
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
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
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	//Función utilizada para regresar al folder padre
	returnToParentFolder(){
		let oldParentFolder = this.lstParentFolder[this.lstParentFolderSize];
		this.idDocument = oldParentFolder;
		this.change.emit(this.idDocument);
		this.getParentFolder(oldParentFolder);
		let that = this;
		setTimeout(function(){
			that.getFolderContent(oldParentFolder);
		},10);
	}

	//Función utilizada para obtener el id del folder padre
	getParentFolder(id){
		this._explorarServices.getParentFolder(localStorage.getItem('token'), id).subscribe(
		    result => {
		        if(result.status===0){
		            this.lstParentFolder = result.exito.parent;
		            this.lstParentFolderSize = this.lstParentFolder.length-1;
		            this.folderName = result.exito.properties.name;
		        }else{
		            this._utilitiesServices.showSwalError(result.status, (error, result): void => {
		                if(result===true){
		                    let that = this;
		                    setTimeout(function(){
		                        that.getParentFolder(id);
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

	//Función utilizada para obtener las propiedades de sistema de un documento
	getSystemProperties(id){
		this._explorarServices.getSystemProperties(localStorage.getItem('token'), id).subscribe(
			result => {
				if(result.status===0){
					this.systemProperties = result.exito.properties;
				}else{
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
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	//Función utilizada para obtener los metadatos de un documento
	getDocMetadata(id){
		this._explorarServices.getDocMetadata(localStorage.getItem('token'), id).subscribe(
			result => {
				if(result.status===0){
					this.metadata = result.exito;
					let that = this;
					setTimeout(function(){
						that.getContentVersion(id);
					},50);
				}else{
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
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	//Modal de propiedades de documento
	propertiesModal(index){
		this.metadata = null;
		this.systemProperties = {
			'name': null,
			'numDocuments':null,
			'numFolders':null,
			'createdBy':null,
			'createdOn':{
				'time':null
			},
			'id':null,
			'contentVersion':null,
			'metadataVersion':null,
			'size':null,
			'modifiedBy':null,
			'modifiedOn':{
				'time':null
			},
			'mimeType':null
		};
		this.historial = null;
		if(this.dataTable.dataRows[index][6]==='document'){
			this.getDocMetadata(this.dataTable.dataRows[index][5]);
		}else{
			this.getSystemProperties(this.dataTable.dataRows[index][5]);
		}
		let that = this;
		let docClass;
		setTimeout(function(){
			if(that.dataTable.dataRows[index][6]==='document'){
				swal({
					title: 'Información de documento',
					html: 
					'<div class="card">'+
					'<div class="card-header card-header-icon" data-background-color="blue">'+
					'<i class="material-icons">description</i>'+
					'</div>'+
					'<h4 class="card-title text-left" style="margin-top: 10px;margin-bottom: 15px;">Propiedades de documento</h4>'+
					'<table>'+
					'<tbody id="properties">'+

					'</tbody>'+
					'</table>'+
					'</div>'+
					'<div class="card">'+
					'<div class="card-header card-header-icon" data-background-color="green">'+
					'<i class="material-icons">settings</i>'+
					'</div>'+
					'<h4 class="card-title text-left" style="margin-top: 10px;margin-bottom: 15px;">Propiedades de sistema</h4>'+
					'<table>'+
					'<tbody>'+
					'<tr>'+
					'<td class="col-xs-5 text-left">'+
					'<label translate>'+
					'Versión'+
					'</label>'+
					'</td>'+
					'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.contentVersion + '.' + that.systemProperties.metadataVersion +'</td>'+
					'</tr>'+
					'<tr>'+
					'<td class="col-xs-5 text-left">'+
					'<label translate>'+
					'Tamaño'+
					'</label>'+
					'</td>'+
					'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.size+'</td>'+
					'</tr>'+
					'<tr>'+
					'<td class="col-xs-5 text-left">'+
					'<label translate>'+
					'Modificado por'+
					'</label>'+
					'</td>'+
					'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.modifiedBy+'</td>'+
					'</tr>'+
					'<tr>'+
					'<td class="col-xs-5 text-left">'+
					'<label translate>'+
					'Modificado el'+
					'</label>'+
					'</td>'+
					'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.convertDate(that.systemProperties.modifiedOn.time)+'</td>'+
					'</tr>'+
					'<tr>'+
					'<td class="col-xs-5 text-left">'+
					'<label translate>'+
					'Añadido por'+
					'</label>'+
					'</td>'+
					'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.createdBy+'</td>'+
					'</tr>'+
					'<tr>'+
					'<td class="col-xs-5 text-left">'+
					'<label translate>'+
					'Añadido el'+
					'</label>'+
					'</td>'+
					'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.convertDate(that.systemProperties.createdOn.time)+'</td>'+
					'</tr>'+
					'<tr>'+
					'<td class="col-xs-5 text-left">'+
					'<label>'+
					'ID'+
					'</label>'+
					'</td>'+
					'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.id+'</td>'+
					'</tr>'+
					'<tr>'+
					'<td class="col-xs-5 text-left">'+
					'<label translate>'+
					'Tipo MIME'+
					'</label>'+
					'</td>'+
					'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.mimeType+'</td>'+
					'</tr>'+
					'</tbody>'+
					'</table>'+
					'</div>'+
					'<div class="card">'+
					'<div class="card-header card-header-icon" data-background-color="red">'+
					'<i class="material-icons">schedule</i>'+
					'</div>'+
					'<h4 class="card-title text-left" style="margin-top: 10px;margin-bottom: 15px;">Historial de versiones</h4>'+
					'<table>'+
					'<thead>'+
					'<th class="col-xs-1">'+
					'<label translate class="text-left">'+
					'Versión'+
					'</label>'+
					'</th>'+
					'<th class="col-xs-4">'+
					'<label translate class="text-left">'+
					'Autor'+
					'</label>'+
					'</th>'+
					'<th class="col-xs-4">'+
					'<label translate class="text-left">'+
					'Fecha'+
					'</label>'+
					'</th>'+
					'<th class="col-xs-3"></th>'+
					'</thead>'+
					'<tbody id="historial">'+

					'</tbody>'+
					'</table>'+
					'</div>',
					showCancelButton: true,
					showConfirmButton: false,
					allowOutsideClick: true,
					allowEscapeKey: true,
					cancelButtonText: "Regresar"
				});

				//Pintar los datos de propiedades de documento
				for(let i = 0; i < that.lstClase.length; i++){
					if(that.lstClase[i].id === that.metadata[0].documentClass){
						docClass = that.lstClase[i].label;
						$('#properties').append(
							'<tr>'+
							'<td class="col-xs-5 text-left">'+
							'<label translate>'+
							'Clase documental'+
							'</label>'+
							'</td>'+
							'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+docClass+'</td>'+
							'</tr>'
						);
						for(let iteratorField = 0; iteratorField < that.lstClase[i].fields.length; iteratorField++){
							if(that.metadata[0].properties[that.lstClase[i].fields[iteratorField].name]){
								let key = that.lstClase[i].fields[iteratorField].label;
								$('#properties').append(
									'<tr>'+
									'<td class="col-xs-5 text-left">'+
									'<label translate>'+
									key+
									'</label>'+
									'</td>'+
									'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+
									that.metadata[0].properties[that.lstClase[i].fields[iteratorField].name]+
									'</td>'+
									'</tr>'
								);
							}
						}
					}
				}

				//Pintar los datos de historial de versiones
				for(let i = 0; i < that.historial.length; i++){
					$('#historial').append(
						'<tr>'+
						'<td class="text-center" style="font-size: 15px;padding-bottom: 5px;">'+
						'1' +
						'</td>'+
						'<td style="font-size: 15px;padding-bottom: 5px;">'+
						that.historial[i].createdBy+
						'</td>'+
						'<td style="padding-right: 5px;font-size: 15px;padding-bottom: 5px;">'+
						that.convertDate(that.historial[i].createdOn.time)+
						'</td>'+
						'<td>'+
						'<button type="button" class="btn btn-primary btn-xs">'+
						'<i class="material-icons">visibility</i>'+
						'</button>'+
						'<button type="button" class="btn btn-primary btn-xs">'+
						'<i class="material-icons">replay</i>'+
						'</button>'+
						'</td>'+
						'</tr>'
					);
				}
	  		}else if(that.dataTable.dataRows[index][6]==='folder'){
			  	swal({
			  		title: 'Información de carpeta',
			  		html: 
			  		'<div class="card">'+
			  		'<div class="card-header card-header-icon" data-background-color="green">'+
			  		'<i class="material-icons">settings</i>'+
			  		'</div>'+
			  		'<h4 class="card-title text-left" style="margin-top: 10px;margin-bottom: 15px;">Propiedades de sistema</h4>'+
			  		'<table>'+
			  		'<tbody>'+
			  		'<tr>'+
			  		'<td class="col-xs-5 text-left">'+
			  		'<label translate>'+
			  		'Nombre'+
			  		'</label>'+
			  		'</td>'+
			  		'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.name+'</td>'+
			  		'</tr>'+
			  		'<tr>'+
			  		'<td class="col-xs-5 text-left">'+
			  		'<label translate>'+
			  		'Número documentos'+
			  		'</label>'+
			  		'</td>'+
			  		'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.numDocuments+'</td>'+
			  		'</tr>'+
			  		'<tr>'+
			  		'<td class="col-xs-5 text-left">'+
			  		'<label translate>'+
			  		'Número subcarpetas'+
			  		'</label>'+
			  		'</td>'+
			  		'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.numFolders+'</td>'+
			  		'</tr>'+
			  		'<tr>'+
			  		'<td class="col-xs-5 text-left">'+
			  		'<label translate>'+
			  		'Añadido por'+
			  		'</label>'+
			  		'</td>'+
			  		'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.createdBy+'</td>'+
			  		'</tr>'+
			  		'<tr>'+
			  		'<td class="col-xs-5 text-left">'+
			  		'<label translate>'+
			  		'Añadido el'+
			  		'</label>'+
			  		'</td>'+
			  		'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.convertDate(that.systemProperties.createdOn.time)+'</td>'+
			  		'</tr>'+
			  		'<tr>'+
			  		'<td class="col-xs-5 text-left">'+
			  		'<label>'+
			  		'ID'+
			  		'</label>'+
			  		'</td>'+
			  		'<td class="col-xs-7 text-left" style="font-size: 15px;padding-bottom: 5px;">'+that.systemProperties.id+'</td>'+
			  		'</tr>'+
			  		'</tbody>'+
			  		'</table>'+
			  		'</div>',
			  		showCancelButton: true,
			  		showConfirmButton: false,
			  		allowOutsideClick: true,
			  		allowEscapeKey: true,
			  		cancelButtonText: "Regresar"
			  	});
			}
		    //Se le da estilo al modal
		    $(".swal2-modal.swal2-show").css('background-color', '#eeeeee');
		},800);
	}

	//Función utilizada para obtener los elementos que contiene una carpeta
	getFolderContent(id){
		this._explorarServices.getFolderContent(localStorage.getItem('token'), localStorage.getItem('domain'), id).subscribe(
			result => {
				if(result.status===0){
					let that = this;
					this.dataTable = result.exito;
					//Se obtiene por primera vez el nombre de la carpeta padre
					this.getSystemProperties(id);
					//Se configura el paginado
					this.pages = [];
					this.selectedPage = 1;
					for(let i = 0; i < this.dataTable.dataRows.length/10; i++){
						this.pages.push("");
					}             
		        }else{
		        	this._utilitiesServices.showSwalError(result.status, (error, result): void => {
		        		if(result===true){
		        			let that = this;
		        			setTimeout(function(){
		        				that.getFolderContent(id);
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

}