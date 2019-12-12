import { Component } from '@angular/core';
import { NgModel } from '@angular/forms';
import { NgClass } from '@angular/common';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';

import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { UtilitiesServices } from '../../../services/utilities.services';
import { BusquedaServices } from '../../../services/busqueda.services';
import { ExplorarServices } from '../../../services/explorar.services';
import { Properties } from '../../../services/properties';

@Component({
	selector: 'busqueda',
	templateUrl: './busqueda.component.html',
	styleUrls: ['./busqueda.component.css'],
	providers: [ PdfViewerServices, UtilitiesServices, BusquedaServices, ExplorarServices, Properties ]
})

export class BusquedaComponent{
	//Variable de manejo de componente de loading
  	public loading = false;
	//Lista de pestañas
	public tabs:any[] = [];
	//Clase documental seleccionada
	/*public claseDocumental;
	//Propiedades de clase documental seleccionada
	public propClaseDocumental = [];
	//Propiedades de clase documental seleccionada para visualización
	public propClaseDocumentalVisualizacion = [];*/

	//Lista de clases documentales existentes
	public lstClase:any[];
	//Lista de criterios
	public lstCriteria:any[];
	//Lista completa de paths
	public lstPaths:any[];
	//Lista de búsquedas almacenadas
	public lstStoredSearch:any[];
	//Contador de resultados
	public numRes = 0;
	//Pestaña seleccionada al momento
	public selectedIndex;
	
	//Lista de paths seleccionados
	/*public lstPath:any[];
	//Objeto de criterios en string
	public criteriaString;
	//Objeto de búsqueda
	public searchData = {
		"info": {
			"name": "Busqueda de Documentos",
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
	};*/

	constructor(private translate: TranslateService, private _explorarServices: ExplorarServices, private _pdfViewerServices:PdfViewerServices, private _utilitiesServices: UtilitiesServices, private _busquedaServices: BusquedaServices){

	}

	ngOnInit(){
		this.getLstPath();
	}

	execute(result){
		let label = '';
		for(let i = 0; i < this.tabs.length; i++){
			if(this.tabs[i].type==='search'){
				this.numRes++;
			}
		}
		let id = this.tabs.length + 1;
		if(this.numRes>0) label = '('+this.numRes+')';
		this.tabs.push({
			'id':id,
			'label': result.nombreTab + ' - Resultado ' + label,
			'type':'search',
			'search': result
		});
		this.selectedIndex = id;
	}

	thumbnails(event){
		if(this.tabs[this.selectedIndex].type==='folder'){
			this.tabs[this.selectedIndex].search = event;
		}
		this.tabChanged({index:this.selectedIndex});
	}

	goToFolderView(index){
		let that = this;
		for(let i = 0; i < that.tabs[that.selectedIndex].search.dataRows.length;i++){
			if(that.tabs[that.selectedIndex].search.dataRows[i][0]===parseInt(index)){
				if(that.tabs[that.selectedIndex].search.dataRows[i][2]==='document'){
					that.tabs.push({
						'id':that.tabs.length + 1,
						'label': '',
						'type':'document',
						'idDoc': that.tabs[that.selectedIndex].search.dataRows[i][1]
					});
					that.selectedIndex = that.tabs.length + 1;
					break;
				}else if(that.tabs[that.selectedIndex].search.dataRows[i][2]==='folder'){
					that._explorarServices.getSystemProperties(localStorage.getItem('token'), that.tabs[that.selectedIndex].search.dataRows[i][1]).subscribe(
						result => {
							if(result.status===0){
								that.tabs.push({
									'id':that.tabs.length + 1,
									'label': result.exito.properties.name,
									'type':'folder',
									'idDoc': that.tabs[that.selectedIndex].search.dataRows[i][1]
								});
								that.selectedIndex = that.tabs.length + 1;
							}
						},
						error => {
							var errorMessage = <any> error;
							console.log(errorMessage);
						}
					);
					break;
				}
			}
		}
	}

	goToDocView(name){
		this._explorarServices.getFolderContent(localStorage.getItem('token'), localStorage.getItem('domain'), this.tabs[this.selectedIndex].idDoc).subscribe(
			result => {
			  	if(result.status===0){
			  		for(let i = 0; i < result.exito.dataRows.length; i++){
			  			if(result.exito.dataRows[i][0]===name){
			  				if(result.exito.dataRows[i][6]==='document'){
								this.tabs.push({
									'id':this.tabs.length + 1,
									'label': name,
									'type':'document',
									'idDoc': result.exito.dataRows[i][5]
								});
								this.selectedIndex = this.tabs.length + 1;
			  				}else if(result.exito.dataRows[i][6]==='folder'){
				  				this.tabs[this.selectedIndex].idDoc = result.exito.dataRows[i][5];
			  				}
			  			}
				  	}
				}
			}, 
			error => {
			  var errorMessage = <any> error;
			  console.log(errorMessage);
			}
		);
	}

	tabChanged(tabChangeEvent){
		this.selectedIndex = tabChangeEvent.index;
		//Configuración de tabla
		let that = this;
		setTimeout(function(){
            $('#datatableResult').off('dblclick touchstart').on('dblclick touchstart', 'tr.clickedRow', function(){
				let index = this.cells[1].childNodes[1].childNodes[0].data;
				that.goToFolderView(index);
			});
			$('#thumbnailsResult').off('dblclick touchstart').on('dblclick touchstart', 'div.thumbMargin', function(){
				let index = this.childNodes[1].innerText;
				for(let i = 0; i < that.tabs[that.selectedIndex].search.dataRows.length;i++){
					if(that.tabs[that.selectedIndex].search.dataRows[i][0]===parseInt(index)){
						if(that.tabs[that.selectedIndex].search.dataRows[i][2]==='document'){
							that.tabs.push({
								'id':that.tabs.length + 1,
								'label': '',
								'type':'document',
								'idDoc': that.tabs[that.selectedIndex].search.dataRows[i][1]
							});
							that.selectedIndex = that.tabs.length + 1;
							break;
						}else if(that.tabs[that.selectedIndex].search.dataRows[i][2]==='folder'){
							that._explorarServices.getSystemProperties(localStorage.getItem('token'), that.tabs[that.selectedIndex].search.dataRows[i][1]).subscribe(
								result => {
									if(result.status===0){
										that.tabs.push({
											'id':that.tabs.length + 1,
											'label': result.exito.properties.name,
											'type':'folder',
											'idDoc': that.tabs[that.selectedIndex].search.dataRows[i][1]
										});
										that.selectedIndex = that.tabs.length + 1;
									}
								},
								error => {
									var errorMessage = <any> error;
									console.log(errorMessage);
								}
							);
							break;
						}
					}
				}	
			});
			$('#thumbnailsFolder').off('dblclick touchstart').on('dblclick touchstart', 'div.thumbMargin', function(){
				let index = this.childNodes[1].innerText;
				for(let i = 0; i < that.tabs[that.selectedIndex].search.dataRows.length;i++){
					if(that.tabs[that.selectedIndex].search.dataRows[i][0]===index){
						if(that.tabs[that.selectedIndex].search.dataRows[i][6]==='document'){
							that.tabs.push({
								'id':that.tabs.length + 1,
								'label': '',
								'type':'document',
								'idDoc': that.tabs[that.selectedIndex].search.dataRows[i][5]
							});
							that.selectedIndex = that.tabs.length + 1;
							break;
						}else if(that.tabs[that.selectedIndex].search.dataRows[i][6]==='folder'){
							that._explorarServices.getSystemProperties(localStorage.getItem('token'), that.tabs[that.selectedIndex].search.dataRows[i][5]).subscribe(
								result => {
									if(result.status===0){
										that.tabs.push({
											'id':that.tabs.length + 1,
											'label': result.exito.properties.name,
											'type':'folder',
											'idDoc': that.tabs[that.selectedIndex].search.dataRows[i][5]
										});
										that.selectedIndex = that.tabs.length + 1;
									}
								},
								error => {
									var errorMessage = <any> error;
									console.log(errorMessage);
								}
							);
							break;
						}
					}
				}	
			});
			$('#datatableFolder').off('dblclick touchstart').on('dblclick touchstart', 'tr.clickedRow', function(){
				let name = this.cells[1].childNodes[1].childNodes[6].childNodes[1].childNodes[0].data;
				this.goToDocView(name);
			});
        },1000);
	}

	changeId(idDoc){
		this.tabs[this.selectedIndex].idDoc = idDoc;
	}

	tabsEvent(name){
		this.tabs[this.selectedIndex].label = name;
	}

	/*selected(selectedClass: any[]){
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

	changeLstSelectedCriteria(lstSelectedCriteria: any[]){
		this.searchData.criteria = lstSelectedCriteria;
		this.criteriaString = JSON.stringify(lstSelectedCriteria);
	}

	orderByEvent(orderBy:any){
		this.searchData.orderBy = orderBy;
	}

	visualizationEvent(visualizationEvent:any[]){
		this.searchData.visualization = visualizationEvent;
	}*/

	showSearch(busqueda){
		this.tabs.push({
			'id':this.tabs.length+1,
			'label':busqueda.info.name,
			'type':'stored',
			'info': busqueda
		})
	}

	getDocument(){
		this._pdfViewerServices.documentClass(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstClase = result.exito;
			    	this.getSearchType();
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

	getSearchType(){
		this._busquedaServices.searchType(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstCriteria = result.exito;
			    	this.getStoredSearch();
                }else{
                	this.loading = false;
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.getSearchType();
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

	//Función utilizada para añadir una nueva pestaña de búsqueda
	setNuevaBusqueda(){
		this.tabs.push({
			'id':this.tabs.length+1,
			'label':'Nueva búsqueda',
			'type':'new'
		})
	}

	removeTab(i){
	    this.tabs.splice(i, 1);
	}

}