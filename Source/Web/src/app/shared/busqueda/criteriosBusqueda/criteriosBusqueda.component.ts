import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

import { TipoBusquedaComponent } from '../tipoBusqueda/tipoBusqueda.component';
import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { UtilitiesServices} from '../../../services/utilities.services';
import { Properties } from '../../../services/properties';

//modals
import swal from 'sweetalert2';

@Component({
	selector: 'criterios-busqueda',
	templateUrl: './criteriosBusqueda.component.html',
	styleUrls: ['./criteriosBusqueda.component.css'],
	providers: [ TipoBusquedaComponent, PdfViewerServices, UtilitiesServices, Properties ]
})

export class CriteriosBusquedaComponent{
	
	@Input('camposClase') claseDocumental;
	@Input('lstCriteria') lstCriteria;
	@Input('idDocumentClass') idDocumentClass;
	//Tipo de dato seleccionado 
	public fieldType:string;
	public selectedCriteria:any;
	public criteria:string;
	public date;
	public dateTimestamp;
	//Elemento seleccionado de lista de campos de clase documental
	public selectedField = [];
	//Tipo de dato seleccionado de lista de campos de clase documental
	public selectedFieldType;
	@Input('selectedCriteria') lstSelectedCriteria:any[];
	@Output() changedCriteria = new EventEmitter<any[]>();

	constructor(private translate: TranslateService, private _tipoBusqueda: TipoBusquedaComponent){	

	}

	ngOnInit(){

	}

	ngOnChanges(changes: SimpleChanges) {
		if(changes.lstSelectedCriteria !== undefined){
			this.selectedField = [];
			for(let i = 0; i < changes.lstSelectedCriteria.currentValue.length; i++){
				for(let iteradorClase = 0; iteradorClase < changes.claseDocumental.currentValue.length; iteradorClase++){
					if((changes.lstSelectedCriteria.currentValue[i].documentClass === changes.claseDocumental.currentValue[iteradorClase].documentClass) && (changes.lstSelectedCriteria.currentValue[i].field === changes.claseDocumental.currentValue[iteradorClase].name)){
						this.selectedField.push(changes.claseDocumental.currentValue[iteradorClase]);
						break;
					}
				}
			}
			for(let i = 0; i < this.selectedField.length; i++){
				this.changeField(i);
			}
			for(let i = 0; i < changes.lstSelectedCriteria.currentValue.length; i++){
				for(let iteradorValor = 0; iteradorValor < changes.lstSelectedCriteria.currentValue[i].value.length; iteradorValor++){
					if(changes.lstSelectedCriteria.currentValue[i].fieldType==='String'){
						this.lstSelectedCriteria[i].writtenValue = changes.lstSelectedCriteria.currentValue[i].value[iteradorValor];
					}else if(changes.lstSelectedCriteria.currentValue[i].fieldType==='DateTime'){
						if(changes.lstSelectedCriteria.currentValue[i].value.length===1){
							this.lstSelectedCriteria[i].selectedDate = new Date(changes.lstSelectedCriteria.currentValue[i].value[iteradorValor]);
						}else{
							this.lstSelectedCriteria[i].selectedDate = [];
							this.lstSelectedCriteria[i].selectedDate.push(new Date(changes.lstSelectedCriteria.currentValue[i].value[iteradorValor]));
						}
					}
                    
				}
			}
			this.changeLstSelectedCriteria();
		}	
	}

	changeLstSelectedCriteria(){
		this.changedCriteria.emit(this.lstSelectedCriteria);
	}

	addCriteria(){
		this.selectedField.push({
			'name':null,
			'documentClass':null
		});
		this.lstSelectedCriteria.push({
			"documentClass": null,
			"field": null,
			"search_criteria": null,
			"value": [
			],
			"editable": true,
			"visible": true,
			"required": false,
			"operator": true,
			//Datos "basura" para manejo de frontend
			"fieldType":null,
			"lstCriteria": null,
			"selectedDate":null,
			"writtenValue":null
		});
		this.changeLstSelectedCriteria();
	}

	modalOptions(index){
		let that = this;
		swal({
			title: 'Opciones',
			html:   '<table style="width: 100%;">'+
						'<tbody>'+
							'<tr>'+
								'<td class="col-xs-6">Editable</td>'+
								'<td class="col-xs-6">'+
									'<div class="togglebutton">'+
	                    				'<label>'+
	                        				'<input type="checkbox" id="editable">'+
	                    				'</label>'+
	                				'</div>'+
								'</td>'+
							'</tr>'+
							'<tr>'+
								'<td class="col-xs-6">Visible</td>'+
								'<td class="col-xs-6">'+
									'<div class="togglebutton">'+
	                    				'<label>'+
	                        				'<input type="checkbox" id="visible">'+
	                    				'</label>'+
	                				'</div>'+
								'</td>'+
							'</tr>'+
							'<tr>'+
								'<td class="col-xs-6">Obligatorio</td>'+
								'<td class="col-xs-6">'+
									'<div class="togglebutton">'+
	                    				'<label>'+
	                        				'<input type="checkbox" id="required">'+
	                    				'</label>'+
	                				'</div>'+
								'</td>'+
							'</tr>'+
							'<tr>'+
								'<td class="col-xs-6">AND/OR</td>'+
								'<td class="col-xs-6">'+
									'<div class="togglebutton">'+
					                    '<label>'+
					                        '<input type="checkbox" id="operator">'+
					                    '</label>'+
					                '</div>'+
								'</td>'+
							'</tr>'+
						'</tbody>'+
					'</table>',
			showCancelButton: true,
			showConfirmButton: false,
			allowOutsideClick: true,
			allowEscapeKey: true,
			cancelButtonText: "Regresar"
		});
		//Editable
		if(that.lstSelectedCriteria[index].editable===true){
			$('#editable').prop('checked', true);
		}else{
			$('#editable').prop('checked', false);
		}
		$('#editable').click(function(){
			if($('input#editable:checked').length===1){
				that.lstSelectedCriteria[index].editable = true;
			}else{
				that.lstSelectedCriteria[index].editable = false;
			}
		});
		//Visible
		if(that.lstSelectedCriteria[index].visible===true){
			$('#visible').prop('checked', true);
		}else{
			$('#visible').prop('checked', false);
		}
		$('#visible').click(function(){
			if($('input#visible:checked').length===1){
				that.lstSelectedCriteria[index].visible = true;
			}else{
				that.lstSelectedCriteria[index].visible = false;
			}
		});
		//Requerido
		if(that.lstSelectedCriteria[index].required===true){
			$('#required').prop('checked', true);
		}else{
			$('#required').prop('checked', false);
		}
		$('#required').click(function(){
			if($('input#required:checked').length===1){
				that.lstSelectedCriteria[index].required = true;
			}else{
				that.lstSelectedCriteria[index].required = false;
			}
		});
		//And/or
		if(that.lstSelectedCriteria[index].operator===true){
			$('#operator').prop('checked', true);
		}else{
			$('#operator').prop('checked', false);
		}
		$('#operator').click(function(){
			if($('input#operator:checked').length===1){
				that.lstSelectedCriteria[index].operator = true;
			}else{
				that.lstSelectedCriteria[index].operator = false;
			}
		});
	}

	removeCriteria(index){
		this.lstSelectedCriteria.splice(index, 1);
		this.selectedField.splice(index, 1);
		this.changeLstSelectedCriteria();
	}

	changeField(index){
		this.lstSelectedCriteria[index].field = this.selectedField[index].name;
		this.lstSelectedCriteria[index].documentClass = this.selectedField[index].documentClass;
		for(let i = 0; i < this.claseDocumental.length; i++){
			if(this.lstSelectedCriteria[index].field===this.claseDocumental[i].name && this.lstSelectedCriteria[index].documentClass===this.claseDocumental[i].documentClass){
				this.lstSelectedCriteria[index].fieldType = this.claseDocumental[i].type;
				break;
			}
		}
		
		/*for(let i = 0; i < this.claseDocumental.length; i++){
			if(this.lstSelectedCriteria[index].field===this.claseDocumental[i].name){
				this.lstSelectedCriteria[index].documentClass = this.claseDocumental[i].documentClass;
				break;
			}
		}*/
		
		for(let i = 0; i < this.lstCriteria.length; i++){
			if(this.lstSelectedCriteria[index].fieldType===this.lstCriteria[i].type){
				this.lstSelectedCriteria[index].lstCriteria = this.lstCriteria[i].search_criteria;
				break;
			}
		}
		this.changeLstSelectedCriteria();
	}

	convertDateToTimestamp(index){
		let lstSelectedDate = this.lstSelectedCriteria[index].selectedDate;
		if(lstSelectedDate.length===2 && lstSelectedDate[1]!==null){
			this.lstSelectedCriteria[index].value = [];
			for(let i = 0; i < lstSelectedDate.length; i++){
				this.lstSelectedCriteria[index].value.push(Math.round(lstSelectedDate[i].getTime()));
			}
		}else if(lstSelectedDate.length===undefined){
			this.lstSelectedCriteria[index].value = [];
			this.lstSelectedCriteria[index].value.push(Math.round(lstSelectedDate.getTime()));
		}
	}

	setValue(index){
		this.lstSelectedCriteria[index].value = [];
		this.lstSelectedCriteria[index].value.push(this.lstSelectedCriteria[index].writtenValue);
	}

	cleanData(index){
		this.lstSelectedCriteria[index].selectedDate = null;
		this.lstSelectedCriteria[index].writtenValue = null;
		this.lstSelectedCriteria[index].value = [];
	}


}