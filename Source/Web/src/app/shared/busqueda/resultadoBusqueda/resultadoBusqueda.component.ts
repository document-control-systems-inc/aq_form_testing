import { Component, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { ExplorarServices } from '../../../services/explorar.services';
import { SocialServices } from '../../../services/social.services';
import { BusquedaServices } from '../../../services/busqueda.services';
import { UtilitiesServices } from '../../../services/utilities.services';
import { Properties } from '../../../services/properties';

//jquery
declare const $: any;
import swal from 'sweetalert2';

import { TranslateService } from '@ngx-translate/core';

@Component({
	selector: 'resultado-busqueda',
	templateUrl: './resultadoBusqueda.component.html',
	styleUrls: ['./resultadoBusqueda.component.css'],
	providers: [ UtilitiesServices, Properties, ExplorarServices, SocialServices, BusquedaServices ]
})

export class ResultadoBusquedaComponent{
	//Variable utilizada para distinguir si es portal o no
	@Input('isPortal') isPortal;
	@Input('searchResult') searchResult;
	@Input('class') lstClase;
	public lstResult = [];
	@Output() thumbnail = new EventEmitter<any[]>();
	@Output() folder = new EventEmitter<any[]>();
	//Paginado
	public pages = [];
	public selectedPage = 1;
	//Filtrado
	public filter;
	//Elementos de modal de propiedades
	public metadata;
	public systemProperties;
	public historial;
	public view = 'table';
	//Context menu//////////////////////////////////////////////////
	//Variable de manejo de componente de loading
  	public loading = false;
	//Listado de elementos seleccionado en tabla
  	public lstSelectedElements = [];
  	//Array para checks de tabla
  	public lstCheck = [];
  	//Lista de paths utilizada en mover/copiar elementos
  	public lstPaths;
  	//Variable utilizada para administrar los templates a mostrar
  	public template:string='explorar';
  	//Variable utilizada para almacenar elemento a copiar/mover
  	public elementToCopy;
  	//Variable de almacenamiento de idParent para manejo de copiar y mover
  	public idParent = null;
  	//Lista de opciones de descarga
  	public lstDownload = null;
	//Variable utilizada para almacenar elemento a descargar
	public elementToDownload;
	//Variable utilizada para almacenar el tipo de descarga almacenada
  	public objDownload = null;
  	//Variable de correo electrónico
  	public email = null;


	constructor(private _busquedaServices:BusquedaServices, private translate: TranslateService, private _socialServices:SocialServices, private _utilitiesServices: UtilitiesServices, private _explorarServices: ExplorarServices){	
		
	}

	ngOnInit(){
		//Se define lista de elementos a mostrar
		this.getLstPath();
		for(let i = 0; i < this.searchResult.dataRows.length; i++){
			let obj = [];
			obj.push(this.searchResult.dataRows[i][0]);
			for(let iteradorData = 1; iteradorData < this.searchResult.headerRow.length - 1; iteradorData++){
				obj.push(this.searchResult.dataRows[i][iteradorData + 2]);
			}
			this.lstResult.push(obj);
			this.lstCheck.push(false);
		}
		//Se configura el paginado
		this.pages = [];
		this.selectedPage = 1;
		for(let i = 0; i < this.searchResult.dataRows.length/10; i++){
			this.pages.push("");
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

	goToFolderViewer(index){
		this.folder.emit(index);
	}

	getLstThumbnails(page){
		let thumbnailIndex = this.searchResult;
		if((this.selectedPage-1)*12 < this.searchResult.dataRows.length){
			thumbnailIndex = this.searchResult.dataRows.length;
		}else{
			thumbnailIndex = this.selectedPage*12;
		}
		for(let i = (this.selectedPage - 1) * 12; i < thumbnailIndex; i++){
			this.getThumbnail(i);
		}
		this.thumbnail.emit([]);
		this.view = 'thumbnail';
	}

	//Función utilizada para obtener la información del árbol
	getThumbnail(index){
		let id = this.searchResult.dataRows[index][1];
		this._explorarServices.getThumbnail(localStorage.getItem('token'), id).subscribe(
			result => {
				let blob = new Blob([result], {type:'image/png'});
				var reader = new FileReader();
				reader.readAsDataURL(blob); 
				let that = this;
				reader.onloadend = function() {
					that.searchResult.dataRows[index].push(reader.result);        
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
	propertiesModal(row){

		let realIndex = row[0] - 1;

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
		
		if(this.searchResult.dataRows[realIndex][2]==='document'){
			this.getDocMetadata(this.searchResult.dataRows[realIndex][1]);
		}else{
			this.getSystemProperties(this.searchResult.dataRows[realIndex][1]);
		}
		let that = this;
		let docClass;
	  setTimeout(function(){
	    if(that.searchResult.dataRows[realIndex][2]==='document'){
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

	    }else if(that.searchResult.dataRows[realIndex][2]==='folder'){
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

	/*/////////////////////////////////////////////////////////////////////////////////
	            BLOQUE DE CONTEXT MENU
	/*/////////////////////////////////////////////////////////////////////////////////

	//Función utilizada para limpiar campos y redirigir a pantalla de explorar
	regresarAExplorar(){
		this.lstSelectedElements = [];
		this.email = null;
		this.elementToCopy = null;
		this.elementToDownload = null;
		this.objDownload = null;
		this.idParent = null;
		this.template='explorar';
	}

	//Función utilizada para compartir documentos o carpetas
	share(id){
		this.loading = true;
		this._explorarServices.share(localStorage.getItem('token'), id).subscribe(
			result => {
				if(result.status===0){
					this.loading = false;
					$.notify({
						icon: 'notifications',
						message: 'El elemento se ha compartido correctamente.'
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
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
						if(result===true){
							let that = this;
							setTimeout(function(){
								that.share(id);
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

	//Función utilizada para añadir documentos o carpetas a favoritos
	setFavorites(id){
		this.loading = true;
		this._explorarServices.setFavorites(localStorage.getItem('token'), id).subscribe(
			result => {
				if(result.status===0){
					this.loading = false;
					$.notify({
						icon: 'notifications',
						message: 'El elemento fue agregado a favoritos.'
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
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
						if(result===true){
							let that = this;
							setTimeout(function(){
								that.setFavorites(id);
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

	//Función utilizada para eliminar documentos o carpetas
	deleteNode(id){
		this.loading = true;
		this._explorarServices.deleteNode(localStorage.getItem('token'), id).subscribe(
			result => {
				if(result.status===0){
					this.loading = false;
					$.notify({
						icon: 'notifications',
						message: 'El elemento se ha eliminado correctamente.'
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
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
						if(result===true){
							let that = this;
							setTimeout(function(){
								that.deleteNode(id);
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

	//Modal de cambiar nombre de documento o carpeta
	changeNameModal(row){
		let that = this;
		swal({
			title: 'Cambiar nombre',
			html: /*'<div class="form-inline">'+
			'<label> Nombre actual: </label>'+ 
			'<div class="form-group" style="margin-top: 0px;">'+
			'<input type="text" class="form-control" id="actualName" value="'+row[0]+'" disabled>'+
			'</div>'+
			'</div>'+*/
			'<div class="form-inline">'+
			'<label> Nuevo nombre: </label>'+ 
			'<div class="form-group" style="margin-top: 0px;">'+
			'<input type="text" class="form-control" id="newName" required="required">'+
			'</div>'+
			'</div>',
			showCancelButton: true,
			allowOutsideClick: false,
			allowEscapeKey: false,
			confirmButtonClass: "btn-danger",
			confirmButtonText: "Agregar"
		}).then(function(){
		// function when confirm button clicked
			let name = $('#newName').val();
			that.changeName(row[1], name);
		});
	}

	//Función de cambio de nombre
	changeName(id, name){
		this.loading = true;
		this._explorarServices.changeName(localStorage.getItem('token'), id, name).subscribe(
			result => {
				if(result.status===0){
					this.loading = false;
					$.notify({
						icon: 'notifications',
						message: 'El nombre del elemento ha sido cambiado correctamente.'
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
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
						if(result===true){
							let that = this;
							setTimeout(function(){
								that.changeName(id, name);
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

	//Modal de creación de template
	setTemplateModal(row){
		let that = this;
		swal({
			title: 'Crear plantilla',
			html: /*'<div class="form-inline">'+
			'<label> Documento seleccionado: </label>'+ 
			'<div class="form-group" style="margin-top: 0px;">'+
			'<input type="text" class="form-control" id="actualName" value="'+row[0]+'" disabled>'+
			'</div>'+
			'</div>'+*/
			'<div class="form-inline">'+
			'<label> Nombre plantilla: </label>'+ 
			'<div class="form-group" style="margin-top: 0px;">'+
			'<input type="text" class="form-control" id="templateName" required>'+
			'</div>'+
			'</div>',
			showCancelButton: true,
			allowOutsideClick: false,
			allowEscapeKey: false,
			confirmButtonClass: "btn-danger",
			confirmButtonText: "Agregar"
		}).then(function(){
		// function when confirm button clicked
			let name = $('#templateName').val();
			that.setTemplate(row[1], name);
		});
	}

	//Función de creación de template
	setTemplate(id, name){
		this.loading = true;
		this._explorarServices.changeName(localStorage.getItem('token'), id, name).subscribe(
			result => {
				if(result.status===0){
					this.loading = false;
					$.notify({
						icon: 'notifications',
						message: 'La plantilla ha sido creada correctamente.'
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
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
						if(result===true){
							let that = this;
							setTimeout(function(){
								that.setTemplate(id, name);
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

	//Modal de comentario(en documento)
	setCommentModal(row){
		let that = this;
		swal({
			title: 'Realizar comentario',
			html: '<div class="input-group comment">'+
			'<div class="form-group" style="margin-top: 0px;padding-bottom: 0px;background-color: white; border-bottom-left-radius: 3px; border-top-left-radius: 3px;">'+
			'<textarea class="form-control" rows="1" style="resize:none;height:80px;" id="comment" autosize></textarea>'+
			'</div>'+
			'<span class="input-group-addon btn btn-primary" style="background-color: #9c27b0;padding-top: 0px;" id="commentButton">'+
			'<i class="fa fa-paper-plane fa-2x" aria-hidden="true"></i>'+
			'</span>'+
			'</div>',
			showConfirmButton: false,
			showCancelButton: false,
			allowOutsideClick: true,
			allowEscapeKey: true,
			confirmButtonClass: "btn-danger",
			confirmButtonText: "Agregar"
		});
		setTimeout(function(){
			$('.comment').on('click', 'span#commentButton', function(){ 
				let comment = $('#comment').val();
				that.setComment(row[1], comment);
			});
		},300);
	}

	setComment(id, comment){
		this.loading = true;
		this._socialServices.setComment(localStorage.getItem('token'), id, comment).subscribe(
			result => {
				if(result.status===0){
					this.loading = false;
					swal.close();
					$.notify({
						icon: 'notifications',
						message: 'Comentario publicado correctamente.'
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
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, null);
				}
			},
			error => {
				this.loading = false;
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
					this.loading = false;
				}else{
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
						if(result===true){
							let that = this;
							setTimeout(function(){
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

	toMoveElement(row){
		this.template = 'move';
		this.elementToCopy = row;
	}

	toCopyElement(row){
		this.template = 'copy';
		this.elementToCopy = row;
	}

	moveElement(){
		this.loading = true;
		this._explorarServices.moveElement(localStorage.getItem('token'), this.elementToCopy[1], this.idParent.id).subscribe(
			result => {
				if(result.status===0){
					this.loading = false;
					this.regresarAExplorar();
					$.notify({
						icon: 'notifications',
						message: 'Se ha cambiado la ubicación del elemento.'
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
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, null);
				}
			},
			error => {
				this.loading = false;
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
			);
	}

	copyElement(){
		this.loading = true;
		this._explorarServices.copyElement(localStorage.getItem('token'), this.elementToCopy[1], this.idParent.id).subscribe(
			result => {
				if(result.status===0){
					this.loading = false;
					this.regresarAExplorar();
					$.notify({
						icon: 'notifications',
						message: 'Elemento copiado correctamente.'
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
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, null);
				}
			},
			error => {
				this.loading = false;
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
			);
	}

	//Redirige a pantalla de descarga
	toDownload(row){
		this.lstDownload = [];
		if(row[2]==='folder'){
			this.lstDownload = [{
				id:'content',
				value:'ZIP de contenido'
			},{
				id:'metadata',
				value:'Información como CSV'
			},{
				id:'contentMetadata',
				value:'Contenido e información'
			},{
				id:'list',
				value:'Lista'
			}]
		}else if(row[2]==='document'){
			this.lstDownload = [{
				id:'zipContent',
				value:'ZIP de contenido'
			},{
				id:'zipContentMetadata',
				value:'ZIP con propiedades'
			},{
				id:'original',
				value:'Contenido original'
			},{
				id:'csv',
				value:'Información como CSV'
			},{
				id:'pdf',
				value:'PDF'
			}]
		}
		this.template = 'download';
		this.elementToDownload = row;
	}

	//Redirige a pantalla de enviar email
	toSendEmail(row){
		this.lstDownload = [];
		if(row[2]==='folder'){
			this.lstDownload = [{
				id:'content',
				value:'ZIP de contenido'
			},{
				id:'metadata',
				value:'Información como CSV'
			},{
				id:'contentMetadata',
				value:'Contenido e información'
			},{
				id:'list',
				value:'Lista'
			}]
		}else if(row[2]==='document'){
			this.lstDownload = [{
				id:'zipContent',
				value:'ZIP de contenido'
			},{
				id:'zipContentMetadata',
				value:'ZIP con propiedades'
			},{
				id:'original',
				value:'Contenido original'
			},{
				id:'csv',
				value:'Información como CSV'
			},{
				id:'pdf',
				value:'PDF'
			}]
		}
		this.template = 'email';
		this.elementToDownload = row;
	}

	//Se definen id y se llama al método de descarga
	multipleSendEmail(){
		let id:String = '';
		for(let i = 0; i < this.lstSelectedElements.length; i++){
			id = id + this.lstSelectedElements[i][1] + '|';
		}
		id = id.slice(0, -1);
		this.sendEmail(id);
	}

	sendEmail(id){
		this.loading = true;
		this._explorarServices.sendEmail(localStorage.getItem('token'), id, this.email, this.objDownload.id).subscribe(
			result => {
				if(result.status===0){
					this.loading = false;
					this.regresarAExplorar();
					$.notify({
						icon: 'notifications',
						message: 'Correo enviado correctamente.'
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
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, null);
				}
			},
			error => {
				this.loading = false;
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
			);
	}

	//Se definen id y se llama al método de descarga
	multipleDownload(){
		let id:String = '';
		for(let i = 0; i < this.lstSelectedElements.length; i++){
			id = id + this.lstSelectedElements[i][1] + '|';
		}
		id = id.slice(0, -1);
		this.download(id);
	}

	//Método de descarga
	download(id){
		this.loading = true;
		this._explorarServices.download(localStorage.getItem('token'), id, this.objDownload.id).subscribe(
			result => {
				this.loading = false;
				var url = window.URL.createObjectURL(result);
				window.open(url);
				this.regresarAExplorar();
			},
			error => {
				this.loading = false;
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
			);
	}

	//Metodo de selección multiple (documentos y carpetas)
	setLstSelectedElements(){
		this.lstSelectedElements = [];
		for(let i = 0; i < this.lstCheck.length; i++){
			if(this.lstCheck[i]===true){
				this.lstSelectedElements.push(this.searchResult.dataRows[i]);
			}
		}
	}

	//Función para redirigir a la pantalla de descarga(descarga de múltiples elementos)
	toMultipleDownload(){
		this.lstDownload = [];
		let isOnlyFolder = false;
		let isOnlyDoc = false;
		let isDocFolder = false;
		for(let i = 0; i < this.lstSelectedElements.length; i++){
			if(this.lstSelectedElements[i][6]==='folder'){
				isOnlyFolder = true;
			}else{
				isOnlyFolder = false;
				break;
			}
		}
		if(isOnlyFolder===false){
			for(let i = 0; i < this.lstSelectedElements.length; i++){
				if(this.lstSelectedElements[i][6]==='document'){
					isOnlyDoc = true;
				}else{
					isOnlyDoc = false;
					break;
				}
			}
			if(isOnlyDoc===false){
				isDocFolder = true;
			}
		}
		if(isOnlyFolder){
			this.lstDownload = [{
				id:'content',
				value:'ZIP de contenido'
			},{
				id:'metadata',
				value:'Información como CSV'
			},{
				id:'contentMetadata',
				value:'Contenido e información'
			},{
				id:'list',
				value:'Lista'
			}]
		}else if(isOnlyDoc){
			this.lstDownload = [{
				id:'zipContent',
				value:'ZIP de contenido'
			},{
				id:'zipContentMetadata',
				value:'ZIP con propiedades'
			},{
				id:'list',
				value:'Lista'
			}]
		}else if(isDocFolder){
			this.lstDownload = [{
				id:'content',
				value:'ZIP de contenido'
			},{
				id:'metadata',
				value:'Información como CSV'
			},{
				id:'contentMetadata',
				value:'Contenido e información'
			},{
				id:'list',
				value:'Lista'
			}]
		}
		this.template = 'multipleDownload';
	}

	toMultipleSendEmail(){
		this.lstDownload = [];
		let isOnlyFolder = false;
		let isOnlyDoc = false;
		let isDocFolder = false;
		for(let i = 0; i < this.lstSelectedElements.length; i++){
			if(this.lstSelectedElements[i][6]==='folder'){
				isOnlyFolder = true;
			}else{
				isOnlyFolder = false;
				break;
			}
		}
		if(isOnlyFolder===false){
			for(let i = 0; i < this.lstSelectedElements.length; i++){
				if(this.lstSelectedElements[i][6]==='document'){
					isOnlyDoc = true;
				}else{
					isOnlyDoc = false;
					break;
				}
			}
			if(isOnlyDoc===false){
				isDocFolder = true;
			}
		}
		if(isOnlyFolder){
			this.lstDownload = [{
				id:'content',
				value:'ZIP de contenido'
			},{
				id:'metadata',
				value:'Información como CSV'
			},{
				id:'contentMetadata',
				value:'Contenido e información'
			},{
				id:'list',
				value:'Lista'
			}]
		}else if(isOnlyDoc){
			this.lstDownload = [{
				id:'zipContent',
				value:'ZIP de contenido'
			},{
				id:'zipContentMetadata',
				value:'ZIP con propiedades'
			},{
				id:'list',
				value:'Lista'
			}]
		}else if(isDocFolder){
			this.lstDownload = [{
				id:'content',
				value:'ZIP de contenido'
			},{
				id:'metadata',
				value:'Información como CSV'
			},{
				id:'contentMetadata',
				value:'Contenido e información'
			},{
				id:'list',
				value:'Lista'
			}]
		}
		this.template = 'multipleEmail';
	}

	/*/////////////////////////////////////////////////////////////////////////////////
	BLOQUE DE CONTEXT MENU
	/*/////////////////////////////////////////////////////////////////////////////////

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