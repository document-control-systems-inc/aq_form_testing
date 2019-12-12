import { Component } from '@angular/core';
import { NgModel } from '@angular/forms';
import { NgClass } from '@angular/common';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';
import { MatTabChangeEvent } from '@angular/material';

import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { UtilitiesServices } from '../../../services/utilities.services';
import { Properties } from '../../../services/properties';

declare const $: any;
declare const contextMenu:any;

@Component({
	selector: 'tareas',
	templateUrl: './tareas.component.html',
	styleUrls: ['./tareas.component.css'],
	providers: [ PdfViewerServices, UtilitiesServices, Properties ]
})

export class TareasComponent{

	public selectedIndex;
	public lstSelectedTasks = [];
	public startedTasks = [];
	public process = [
		{
			'id':1,
			'processName':'Proceso 1'
		},
		{
			'id':2,
			'processName':'Proceso 2'
		},
		{
			'id':3,
			'processName':'Proceso 3'
		},
		{
			'id':4,
			'processName':'Proceso 4'
		}
	];
	public tasks = [
		{
			'status':1,
			'id':'XXX-0000001',
			'taskName':'Aprobación documento',
			'responsable':'Usuario01',
			'process':'Revisión de documentos',
			'selected':false,
			"createdOn": {
				"time": 1513024746070, 
				"timezone": null
			}
		},
		{
			'status':1,
			'id':'XXX-0000002',
			'taskName':'En espera de firma',
			'responsable':'Aprobador01',
			'process':'Firma de documentos',
			'selected':false,
			"createdOn": {
				"time": 1513024746070, 
				"timezone": null
			}
		},
		{
			'status':2,
			'id':'XXX-0000003',
			'taskName':'Corrección de documento',
			'responsable':'[ Revisores ]',
			'process':'Revisión de documentos',
			'selected':false,
			"createdOn": {
				"time": 1513024746070, 
				"timezone": null
			}
		},
		{
			'status':3,
			'id':'XXX-0000004',
			'taskName':'Llenado de información',
			'responsable':'[ Secretaría ]',
			'process':'Nueva carta',
			'selected':false,
			"createdOn": {
				"time": 1513024746070, 
				"timezone": null
			}
		},
		{
			'status':3,
			'id':'XXX-0000005',
			'taskName':'Aprobación eliminar',
			'responsable':'Usuario01',
			'process':'Eliminación de documentos',
			'selected':false,
			"createdOn": {
				"time": 1513024746070, 
				"timezone": null
			}
		}
	]
	
	constructor(private translate: TranslateService, private _pdfViewerServices:PdfViewerServices, private _utilitiesServices: UtilitiesServices){
	    
	}

	ngOnInit(){
		
	}

	ngAfterViewInit(){

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

	//Función utilizada para crear el listado de tareas seleccionadas
	checkTask(){
		this.lstSelectedTasks = [];
		for(let i = 0; i < this.tasks.length; i++){
			if(this.tasks[i].selected===true){
				this.lstSelectedTasks.push(this.tasks[i]);
			}
		}
	}

	//Función utilizada para inicializar nuevas tareas
	startNewTasks(){
		for(let i=0; i < this.lstSelectedTasks.length; i++){
			this.startedTasks.push(this.lstSelectedTasks[i]);
			this.selectedIndex = i + this.startedTasks.length + 2;
		}

	}

	cerrar(cerrarEvent:any){
		this.removeTab(cerrarEvent);
	}

	removeTab(i){
	    this.startedTasks.splice(i, 1);
	}

	tabChanged(tabChangeEvent: MatTabChangeEvent){
		if(window.innerWidth > 800 && window.innerHeight > 600) {
			$('[data-toggle="popover"]').popover({
		        placement : 'bottom',
		        container: 'body',
		        trigger : 'hover'
		    });
		}		
		this.selectedIndex = tabChangeEvent.index;
	}

}