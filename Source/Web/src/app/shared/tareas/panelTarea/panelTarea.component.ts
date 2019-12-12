import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

declare const $:any;
declare const contextMenu:any;

import { Properties } from '../../../services/properties';
import { ExplorarServices } from '../../../services/explorar.services';
import { UtilitiesServices } from '../../../services/utilities.services';

import { TranslateService } from '@ngx-translate/core';
import { TreeModel, NodeEvent, Ng2TreeSettings, NodeMenuItemAction } from 'ng2-tree';

@Component({
	selector: 'panel-tarea',
	templateUrl: './panelTarea.component.html',
	styleUrls: ['./panelTarea.component.css'],
	providers: [ Properties, ExplorarServices, UtilitiesServices ]
})

export class PanelTareaComponent{
	@ViewChild('treeComponent') public treeComponent;
	@Input('id') idTarea;
	@Input('index') index;
	//evento de regresar a explorar
	@Output() cerrar = new EventEmitter<any>();
	public lstDocumentos:any[] = [];
	public docPanel = true;
	public filter;
	public pages = [];
	public selectedPage = 1;
	public indexDoc = null;
	//Variable temporal utilizada para controlar el evento de doble click
	public tableRequest = 0;

	public infoTarea = {
		"title": "Aprobación de contrato",
		"fields": [
			{
				"id": "name",
				"label": "Nombre",
				"type": "String",
				"required": true
			},
			{
				"id": "date",
				"label": "Fecha del Contrato",
				"type": "Date",
				"required": false
			}
		],
		"documents": {
			"visible": true,
			"maxDocs": 3,
			"minDocs": 1
		},
		"actions":[
			{
				"id": "continue",
				"class": "success",
				"label": "Aprobar Contrato",
				"value": "true"
			},
			{
				"id": "reject",
				"class":"danger",
				"label": "Rechazar Contrato",
				"value": "true"
			}
		]
	};

	public objTarea = {
		"idTask": null,
		"fields": [
			
		],
		"documents": [
			
		],
		"actions":{
			
		}
	};

	public tree:TreeModel = null;

	public dataTable = null;

	public settings: Ng2TreeSettings = {
	    rootIsVisible: false
	};

	constructor(private translate: TranslateService, private _utilitiesServices: UtilitiesServices, private _explorarServices: ExplorarServices){	
		
	}

	ngOnInit(){
		this.getFolder();
	}

	ngAfterViewInit(){
		this.objTarea.idTask = this.idTarea;
		for(let i = 0; i < this.infoTarea.fields.length; i++){
			this.objTarea.fields.push({
				"id": this.infoTarea.fields[i].id,
				"value": null
			});
		}
	}

	addDocument(){
		this.lstDocumentos.push({
			'idDoc':null,
			'nombre':null
		});
	}

	explorarDocs(index){
		this.objTarea.documents.splice(index, 1);
		this.docPanel = false;
		this.indexDoc = index;
	}

	deleteDocument(index){
		this.lstDocumentos.splice(index, 1);
		this.objTarea.documents.splice(index, 1);
	}

	regresarATareas(){
		this.cerrar.emit(this.index);
	}

	//Función utilizada para obtener la información del árbol
	getFolder(){
		this._explorarServices.getFolder(localStorage.getItem('token')).subscribe(
			result => {
				if(result.status===0){
					this.tree = result.exito;
				}else{
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
						if(result===true){
							let that = this;
							setTimeout(function(){
								that.getFolder();
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

	//Función utilizada para obtener los elementos que contiene una carpeta
	getFolderContent(id){
	      this._explorarServices.getFolderContent(localStorage.getItem('token'), localStorage.getItem('domain'), id).subscribe(
	          result => {
	              if(result.status===0){
	                  let that = this;
	                  this.dataTable = result.exito;
	                  //Se configura el paginado
						this.pages = [];
						this.selectedPage = 1;
						for(let i = 0; i < this.dataTable.dataRows.length/10; i++){
							this.pages.push("");
						}
	                  
	                  //Contador de consultas de tabla
	                  this.tableRequest++;
	                  //Se añade evento de doble click
	                  if (this.dataTable.dataRows.length<1) {
	                    this.tableRequest = 0;
	                  }
	                  if(this.tableRequest<=1){
	                    setTimeout(function(){
	                      //Se añade el evento de doble click 
	                      $('#datatable').on('dblclick touchstart', 'tr.clickedRow', function(){ 
	                        //let index = this.rowIndex - 1;
	                        //let nombreSeleccionado = that.dataTable.dataRows[index][0];
	                        let nombreSeleccionado = this.cells[1].childNodes[1].childNodes[6].innerText;
	                        let index;
	                        for(let i = 0; i < that.dataTable.dataRows.length; i++){
	                          if(nombreSeleccionado===that.dataTable.dataRows[i][0]){
	                            index = i;
	                          }
	                        }
	                        if(that.dataTable.dataRows[index][6]==='folder'){
	                          that.filter = '';
	                          //that.folderName = that.dataTable.dataRows[index][0];
	                          //that.getParentFolder(that.dataTable.dataRows[index][5]);
	                          setTimeout(function(){
	                            let id = that.dataTable.dataRows[index][5];
	                            that.getFolderContent(that.dataTable.dataRows[index][5]);
	                            let ids = that.dataTable.dataRows[index][5];
	                            let node = that.treeComponent.getControllerByNodeId(ids);
	                            node.select();
	                          },10);
	                        }
	                      });
	                    },100);
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

	//Función para ir a página anterior
	prevPage(){
		if(this.selectedPage>1){
			this.selectedPage--;
		}
	}

	//Función para ir a la página siguiente
	nextPage(){
		if(this.selectedPage<this.pages.length){
			this.selectedPage++;
		}
	}

	//Evento para obtener el id de carpeta del árbol
	public selectedFolder(e: NodeEvent): void {
		let that = this;
		setTimeout(function(){
			that.getFolderContent(e.node.node.id);
		},10);
		let node = this.treeComponent.getControllerByNodeId(e.node.node.id);
		node.expand();
	}

	cancelarSeleccion(){
		this.docPanel = true;
		this.dataTable = null;
		this.indexDoc = null;
		this.tree = null;
		this.getFolder();
	}

	convertDateToTimestamp(index){
		let selectedDate = this.objTarea.fields[index].date;
		this.objTarea.fields[index].value = Math.round(selectedDate.getTime());
	}

	public onMenuItemSelected(event):void{
		debugger;
	}

	public onRightClick(event):void{
		$(".node-menu-content").css('width', '222px');
	}

	selectDocument(index){
		this.lstDocumentos[this.indexDoc] = {
			'idDoc':null,
			'nombre':null
		};
		for(let i = 0; i < this.dataTable.dataRows.length; i++){
			this.dataTable.dataRows[i].selected = false;
		}
		this.lstDocumentos[this.indexDoc].idDoc = this.dataTable.dataRows[index][5];
		this.lstDocumentos[this.indexDoc].nombre = this.dataTable.dataRows[index][0];
		this.dataTable.dataRows[index].selected = true;
	}

	setAction(index){
		this.objTarea.actions = this.infoTarea.actions[index];
		this.regresarATareas();
	}

	addToDocList(){
		this.objTarea.documents.push(this.lstDocumentos[this.indexDoc].idDoc);
		this.cancelarSeleccion();
	}

}