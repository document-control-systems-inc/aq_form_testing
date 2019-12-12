import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { Http, Request } from '@angular/http';

import { CanvasWhiteboardComponent } from 'ng2-canvas-whiteboard';

import * as Chartist from 'chartist';

declare const $: any;
declare const Tiff: any;

import { PdfViewerServices } from '../../services/pdfViewer.services';
import { ExplorarServices } from '../../services/explorar.services';
import { BusquedaServices } from '../../services/busqueda.services';
import { UtilitiesServices } from '../../services/utilities.services';
import { Properties } from '../../services/properties';

import { DropzoneComponent , DropzoneDirective, DropzoneConfigInterface } from 'ngx-dropzone-wrapper';

@Component({
	selector: 'portal',
	templateUrl: './portal.component.html',
	viewProviders: [ CanvasWhiteboardComponent ],
	providers: [ PdfViewerServices, ExplorarServices, UtilitiesServices, BusquedaServices, Properties ],
  	styleUrls: ['./portal.component.css']
})

export class PortalComponent{

	//Variable de manejo de loading
	public loading;
	//Listado de cúsquedas almacenadas
	public lstStoredSearch;
	//Variable de manejo de pantalla
	public template = 'portal';
	//Variable de almacenaje de búsqueda
	public searchResult;
	//Variable de clases documentales
	public lstClase;
	//Id de elemento seleccionado
	public idDoc = null;

	ngOnInit(){
		this.getDocument();
	}


	constructor(private _busquedaServices:BusquedaServices, private _utilitiesServices:UtilitiesServices, private _pdfViewerServices:PdfViewerServices, private _explorarServices:ExplorarServices, public http:Http){

	}

	getDocument(){
		this._pdfViewerServices.documentClass(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstClase = result.exito;
			    	this.getStoredSearch();
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

	getStoredSearch(){
		this._busquedaServices.getStoredSearch(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstStoredSearch = result.exito;
			    	this.loading = false;
                }else{
                	this.loading = false;
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.getStoredSearch();
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

	executeSearch(search){
		this._busquedaServices.executeSearch(localStorage.getItem('token'), JSON.stringify(search)).subscribe(
			result => {
			    if(result.status===0){
			    	this.searchResult = result.exito;
			    	this.template = 'result';
			    	let that = this;
			    	setTimeout(function(){
						$('#datatableResult').off('dblclick touchstart').on('dblclick touchstart', 'tr.clickedRow', function(){
							let index = this.cells[1].childNodes[1].childNodes[0].data;
							that.goToFolderView(index);
						});

			    	},500);	
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.executeSearch(search);
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

	thumbnails(event){
		let that = this;
    	setTimeout(function(){
    		$('#thumbnailsResult').off('dblclick touchstart').on('dblclick touchstart', 'div.thumbMargin', function(){
				debugger;
				let index = this.childNodes[1].innerText;
				that.goToFolderView(index);
			});
    	},500);
	}

	//Función de resultado busqueda para dar funcionalidad a la navegación
	goToFolderView(index){
		for(let i = 0; i < this.searchResult.dataRows.length;i++){
			if(this.searchResult.dataRows[i][0]===parseInt(index)){
				if(this.searchResult.dataRows[i][2]==='document'){
					this.idDoc = this.searchResult.dataRows[i][1];
					this.template = 'viewer';
					let that = this;
			    	setTimeout(function(){
			    	},500);
					break;
				}else if(this.searchResult.dataRows[i][2]==='folder'){
					this.idDoc = this.searchResult.dataRows[i][1];
					this.template = 'folder';
					let that = this;
			    	setTimeout(function(){
			    	},500);
					break;
				}
			}
		}
	}
}