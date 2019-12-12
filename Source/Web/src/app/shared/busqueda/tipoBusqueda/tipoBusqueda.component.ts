import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

@Component({
	selector: 'tipo-busqueda',
	templateUrl: './tipoBusqueda.component.html',
	styleUrls: ['./tipoBusqueda.component.css'],
	providers: [ ]
})

export class TipoBusquedaComponent{
	@Input('id') id;
	@Input('clases') lstClase;
	@Input('paths') lstPaths;
	@Input('searchData') searchData;
	//Se distingue si se buscará en subcarpetas
	@Input('subfolder') subfolder;
	@Input('name') name;
	@Output() selected = new EventEmitter<any[]>();
	@Output() class = new EventEmitter<any[]>();
	@Output() search = new EventEmitter<any[]>();
	@Output() subcarpetas = new EventEmitter<any[]>();
	public claseDocumental:any[];
	public claseCarpetas:any[];
	public searchType = 1;
	//prueba de multiselect
	public multipleSelected = [];
	public placeholder = "Filtra aquí...";

	constructor(private translate: TranslateService){
		setTimeout(function(){	
			$('.selected-container-item').removeClass('btn-default');
			$('.caret').hide();
		},1);
	}

	ngOnInit(){
		//Se define el domain por default
		if(this.name===null){
			let domain = [];
			domain.push(this.lstPaths[0]);
			this.multipleSelected = domain;
			this.setLstPath();
			//Se define la clase documental por default
			let lstClass = [];
			lstClass.push(this.lstClase[0]);
			this.claseDocumental = lstClass;
			this.setLstClaseDocumental();
		}	
	}

	ngOnChanges(changes: SimpleChanges) {
		if(changes.searchData !== undefined){
			//Primero se coloca la lstPath
			let lstPath = [];
			for(let i = 0; i < changes.searchData.currentValue.searchIn.length; i++){
				lstPath.push({
					id:changes.searchData.currentValue.searchIn[i]
				});
			}
			this.multipleSelected = lstPath;

			//Ahora se coloca la lstClase
			let lstClassSelected = [];
			for(let i = 0; i < changes.searchData.currentValue.documentClass.length; i++){
				for(let iteradorClass = 0; iteradorClass < this.lstClase.length; iteradorClass++){
					if(this.lstClase[iteradorClass].id === changes.searchData.currentValue.documentClass[i]){
						lstClassSelected.push(this.lstClase[iteradorClass]);
						break;
					}
				}
			}
			this.claseDocumental = lstClassSelected;
			this.class.emit(this.claseDocumental);

			//Seleccionamos el tipo busqueda
			let type = true;
			for(let i = 0; i < this.claseDocumental.length; i++){
				if(this.claseDocumental[i].name === 'folder'){
					type = false;
					break;
				}
			}
			if(type===true){
				this.searchType = 1;
			}else{
				this.searchType = 2;
			}
		}
	}

	setSubfolder(){
		this.subcarpetas.emit(this.subfolder);
	}

	setLstClaseDocumental(){
		this.claseCarpetas = [];
		this.selected.emit(this.claseDocumental);
	}

	clearLstCarpetas(){
		this.claseCarpetas = [];
		this.selected.emit(this.claseCarpetas);
	}

	clearLstClaseDocumental(){
		this.claseDocumental = [];
		this.claseCarpetas = [];
		for(let i=0; i < this.lstClase.length; i++){
			if(this.lstClase[i].id==='4'){
				this.claseCarpetas.push(this.lstClase[i]);
			}
		}
		this.selected.emit(this.claseCarpetas);
	}

	setLstPath(){
		this.search.emit(this.multipleSelected);
		setTimeout(function(){
			$('.selected-item-item').removeClass('btn-primary').addClass('btn-primary');
			$('.item').removeClass('whiteBackground').addClass('whiteBackground');
		},1);
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