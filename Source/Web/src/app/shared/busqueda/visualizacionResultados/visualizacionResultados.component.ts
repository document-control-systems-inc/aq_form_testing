import { Component, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

@Component({
	selector: 'visualizacion',
	templateUrl: './visualizacionResultados.component.html',
	styleUrls: ['./visualizacionResultados.component.css'],
	providers: [  ]
})

export class VisualizacionResultadosComponent{

	//Lista de clases documentales seleccionada
	@Input('camposClase') lstPropiedades;
	//Lista de criterios de búsqueda seleccionados(en string)
	@Input('criteria') lstCriterios;
	@Input('orderByObj') storedOrderBy;
	//Lista de propiedades seleccionadas
	public lstSelected = [];
	//id de propiedades seleccionadas lstPropiedades
	public selectedProp;
	//id de propiedades seleccionadas lstSelected;
	public selectedList = {
		'name': null,
		'documentClass': null
	};
	//Elemento por el cual será ordenada la búsqueda
	public orderByObj;
	//Lista de visualización con formato final
	public visualization = [];
	//Lista de ordenamiento con formato final
	public orderBy = {
		'documentClass':null,
		'field':null,
		'ascending':null
	}
	//Evento de manejo de objeto de visualización
	@Output() visualizationEvent = new EventEmitter<any[]>();
	//Evento de manejo de objeto de ordenamiento
	@Output() orderByEvent = new EventEmitter<any>();

	@Input('id') id;

	constructor(private translate: TranslateService){	
		
	}

	ngOnInit(){

	}

	//Todo el proceso de conexión entre componente de criterios y visualización, incluidos los events se manejan aquí
	ngOnChanges(changes: SimpleChanges){
		if(changes.lstPropiedades && this.lstPropiedades!==undefined){
			this.lstSelected = [];
			this.orderByObj = undefined;
			this.orderBy.ascending = null;
		}
		if(changes.lstCriterios && this.lstCriterios!==undefined){
			let json = JSON.parse(this.lstCriterios);
			if(changes.lstCriterios.previousValue!==undefined){
				let oldJson = JSON.parse(changes.lstCriterios.previousValue);
				/*if(oldJson.length>=json.length){
					debugger;
					for(let iteradorOld = 0; iteradorOld < oldJson.length; iteradorOld++){
						let cont = 0;
						for(let iteradorNew = 0; iteradorNew < json.length; iteradorNew++){
							if(oldJson[iteradorOld]!==json[iteradorNew]){
								cont++;
							}
						}
						if(cont===json.length){
							alert('Se supone que borraste el index '+iteradorOld);
						}
					}
				}*/
				/*let diferencias= this.diff(oldJson, json);
				for(let obj in diferencias){
					if(diferencias[obj]===undefined){
						this.deleted.push(oldJson[obj]);
					}
				}*/
				for(let obj in oldJson){
					this.selectedList = {
						'name': oldJson[obj].field,
						'documentClass': oldJson[obj].documentClass
					};
					this.setToPropList();
				}
			}
			//Hacer el push a lstSelected
			this.selectedProp = [];
			for(let iteradorJson = 0; iteradorJson<json.length; iteradorJson++){
				if(json[iteradorJson].field!==null){
					this.selectedProp.push({
						'name': json[iteradorJson].field,
						'documentClass': json[iteradorJson].documentClass
					});
				}
			}
			if(this.selectedProp!==undefined && this.selectedProp!==null){
				this.setSelectedProperties();
			}
			
		}
		if(changes.storedOrderBy && this.storedOrderBy !== undefined){
			for(let i = 0; i < this.lstSelected.length; i++){
				if(this.lstSelected[i].name === changes.storedOrderBy.currentValue.field && this.lstSelected[i].documentClass === changes.storedOrderBy.currentValue.documentClass){
					this.orderByObj = this.lstSelected[i];
				}
			}
			this.orderBy.ascending = changes.storedOrderBy.currentValue.ascending;
		}
	}

	//Función utilizada para seleccionar una o varias propiedades
	setSelectedProperties(){
		for(let i = 0; i < this.selectedProp.length; i++){
			for(let iteradorProp = 0; iteradorProp < this.lstPropiedades.length; iteradorProp++){
				if(this.selectedProp[i].name===this.lstPropiedades[iteradorProp].name && this.selectedProp[i].documentClass===this.lstPropiedades[iteradorProp].documentClass){
					this.lstSelected.push(this.lstPropiedades[iteradorProp]);
					this.lstPropiedades.splice(iteradorProp, 1);
				}
			}
		}
		this.visualization = [];
		for(let iterador = 0; iterador < this.lstSelected.length; iterador++){
			let obj = {
				'documentClass':this.lstSelected[iterador].documentClass,
				'field':this.lstSelected[iterador].name
			}
			this.visualization.push(obj);
		}
		this.visualizationEvent.emit(this.visualization);
		this.selectedProp = null;
	}

	//Función utilizada para quitar la selección de una o varias propiedades
	setToPropList(){
		for(let iteradorSelected = 0; iteradorSelected < this.lstSelected.length; iteradorSelected++){
			if(this.selectedList.name===this.lstSelected[iteradorSelected].name && this.selectedList.documentClass===this.lstSelected[iteradorSelected].documentClass){
				this.lstPropiedades.push(this.lstSelected[iteradorSelected]);
				this.lstSelected.splice(iteradorSelected, 1);
				this.lstPropiedades.sort((a, b) => a.documentClass - b.documentClass);
			}
		}
		this.visualization = [];
		for(let iterador = 0; iterador < this.lstSelected.length; iterador++){
			let obj = {
				'documentClass':this.lstSelected[iterador].documentClass,
				'field':this.lstSelected[iterador].name
			}
			this.visualization.push(obj);
		}
		this.visualizationEvent.emit(this.visualization);
		this.selectedList = null;
	}

	//Función utilizada para mover una propiedad seleccionada hacia arriba
	moveUp(){
		let originalIndex = this.lstSelected.indexOf(this.selectedList);
		let newIndex = originalIndex - 1;
		let originalElement = this.lstSelected[originalIndex];
		let newElement = this.lstSelected[newIndex];
		this.lstSelected.splice(newIndex, 2);
    	this.lstSelected.splice(newIndex, 0, originalElement);
    	this.lstSelected.splice(originalIndex, 0, newElement);   	
	}

	//Función utilizada para mover una propiedad seleccionada hacia abajo
	moveDown(){
		let originalIndex = this.lstSelected.indexOf(this.selectedList);
		let newIndex = originalIndex + 1;
		let originalElement = this.lstSelected[originalIndex];
		let newElement = this.lstSelected[newIndex];
		this.lstSelected.splice(originalIndex, 2);
    	this.lstSelected.splice(originalIndex, 0, newElement);
    	this.lstSelected.splice(newIndex, 0, originalElement);   	
	}

	//Función utilizada para definir los valores del objeto de ordenamiento
	setOrderBy(){
		this.orderBy.documentClass = this.orderByObj.documentClass;
		this.orderBy.field = this.orderByObj.name;
		this.orderByEvent.emit(this.orderBy);
	}

	/*//Función para detectar si un json está vacío
	isEmptyObject(obj) {
        var name;
        for (name in obj) {
            return false;
        }
        return true;
    };

	//Función para detectar diferencias entre json
	diff(obj1, obj2){
		var result = {};
        var change;
        for (var key in obj1) {
            if (typeof obj2[key] == 'object' && typeof obj1[key] == 'object') {
                change = this.diff(obj1[key], obj2[key]);
                if (this.isEmptyObject(change) === false) {
                    result[key] = change;
                }
            }
            else if (obj2[key] != obj1[key]) {
                result[key] = obj2[key];
            }
        }
        return result;
	}*/

}