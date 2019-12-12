import { Component } from '@angular/core';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { UtilitiesServices } from '../../../services/utilities.services';
import { DocClassServices } from '../../../services/docclass.services';
import { Properties } from '../../../services/properties';

import * as $ from 'jquery';
import swal from 'sweetalert2';

@Component({
	selector: 'doc-class',
	templateUrl: './docclass.component.html',
	styleUrls: ['./docclass.component.css'],
	providers: [ UtilitiesServices, DocClassServices, Properties ]
})

export class DocClassComponent{

	public newClass = {
		'name':null,
		'label':null,
		'fields':[]
	};
	public selectedId;
	public updateClass = {
		'id':null,
		'name':null,
		'label':null,
		'fields':[]
	};
	public lstClasesDoc = [];
	public lstTipos = [
		{
			'name':'String',
			'label':'String'
		},
		{
			'name':'Number',
			'label':'Número'
		},
		{
			'name':'Date',
			'label':'Fecha'
		}
	];

	constructor(private _docClassServices:DocClassServices, private _utilitiesServices:UtilitiesServices){
		let that = this;
		setTimeout(function(){
			that.addStyle();
		},1);
	}

	ngOnInit(){
		this.getDocument();
	}

	addStyle(){
		$('.selected-container-text').css("height", "41px");	
		$('.selected-container-text').css("margin-top", "0px");
		$('.mat-tab-body-content').css("min-height", "300px");
		$('.selected-container-item').removeClass('btn-default');
		$('.caret').hide();
	}

	/* Funciones de select */

	getDocument(){
		this._docClassServices.documentClass(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstClasesDoc = result.exito;  	
                }else{
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
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	/*Funciones de insert*/

	addNewField(){
		this.newClass.fields.push({
			'name':null,
			'label':null,
			'type':null,
			'properties':{
				'size':-1,
				'required':false,
				'email':false,
				'url':false
			}
		});
	}

	deleteNewField(index){
		this.newClass.fields.splice(index, 1);
	}

	modalNewProperties(index){
		let that = this;
		swal({
			title: 'Propiedades',
			html:   '<table style="width: 100%;">'+
						'<tbody>'+
							'<tr>'+
								'<td class="col-xs-6">Tamaño</td>'+
								'<td class="col-xs-6">'+
                    				'<div class="form-group" style="margin-top: 0px;">'+
										'<input type="number" class="form-control" id="size">'+
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
								'<td class="col-xs-6">Correo electrónico</td>'+
								'<td class="col-xs-6">'+
									'<div class="togglebutton">'+
	                    				'<label>'+
	                        				'<input type="checkbox" id="email">'+
	                    				'</label>'+
	                				'</div>'+
								'</td>'+
							'</tr>'+
							'<tr>'+
								'<td class="col-xs-6">URL</td>'+
								'<td class="col-xs-6">'+
									'<div class="togglebutton">'+
					                    '<label>'+
					                        '<input type="checkbox" id="url">'+
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
		//Tamaño
		$('#size').val(this.newClass.fields[index].properties.size);
		$('#size').on('input', function(){
			that.newClass.fields[index].properties.size = $('#size').val();
		});
		//Obligatorio
		if(this.newClass.fields[index].properties.required===true){
			$('#required').prop('checked', true);
		}else{
			$('#required').prop('checked', false);
		}
		$('#required').click(function(){
			if($('input#required:checked').length===1){
				that.newClass.fields[index].properties.required = true;
			}else{
				that.newClass.fields[index].properties.required = false;
			}
		});
		//Correo
		if(this.newClass.fields[index].properties.email===true){
			$('#email').prop('checked', true);
		}else{
			$('#email').prop('checked', false);
		}
		$('#email').click(function(){
			if($('input#email:checked').length===1){
				that.newClass.fields[index].properties.email = true;
			}else{
				that.newClass.fields[index].properties.email = false;
			}
		});
		//URL
		if(this.newClass.fields[index].properties.url===true){
			$('#url').prop('checked', true);
		}else{
			$('#url').prop('checked', false);
		}
		$('#url').click(function(){
			if($('input#url:checked').length===1){
				that.newClass.fields[index].properties.url = true;
			}else{
				that.newClass.fields[index].properties.url = false;
			}
		});
	}

	/*Funciones de update*/

	setUpdateDocClass(){
		for(let i = 0; i < this.lstClasesDoc.length; i++){
			if(this.lstClasesDoc[i].id === this.selectedId){
				this.updateClass.id = this.lstClasesDoc[i].id;
				this.updateClass.label = this.lstClasesDoc[i].label;
				this.updateClass.name = this.lstClasesDoc[i].name;
				this.updateClass.fields = [];
				for(let iteradorField = 0; iteradorField < this.lstClasesDoc[i].fields.length; iteradorField++){
					this.updateClass.fields.push(this.lstClasesDoc[i].fields[iteradorField]);
				}
			}
		}
	}

	addUpdateField(){
		this.updateClass.fields.push({
			'name':null,
			'label':null,
			'type':null,
			'properties':{
				'size':-1,
				'required':false,
				'email':false,
				'url':false
			}
		});
	}

	deleteUpdateField(index){
		this.updateClass.fields.splice(index, 1);
	}

	modalUpdateProperties(index){
		let that = this;
		swal({
			title: 'Propiedades',
			html:   '<table style="width: 100%;">'+
						'<tbody>'+
							'<tr>'+
								'<td class="col-xs-6">Tamaño</td>'+
								'<td class="col-xs-6">'+
                    				'<div class="form-group" style="margin-top: 0px;">'+
										'<input type="number" class="form-control" id="sizeUpdate">'+
									'</div>'+
								'</td>'+
							'</tr>'+
							'<tr>'+
								'<td class="col-xs-6">Obligatorio</td>'+
								'<td class="col-xs-6">'+
									'<div class="togglebutton">'+
	                    				'<label>'+
	                        				'<input type="checkbox" id="requiredUpdate">'+
	                    				'</label>'+
	                				'</div>'+
								'</td>'+
							'</tr>'+
							'<tr>'+
								'<td class="col-xs-6">Correo electrónico</td>'+
								'<td class="col-xs-6">'+
									'<div class="togglebutton">'+
	                    				'<label>'+
	                        				'<input type="checkbox" id="emailUpdate">'+
	                    				'</label>'+
	                				'</div>'+
								'</td>'+
							'</tr>'+
							'<tr>'+
								'<td class="col-xs-6">URL</td>'+
								'<td class="col-xs-6">'+
									'<div class="togglebutton">'+
					                    '<label>'+
					                        '<input type="checkbox" id="urlUpdate">'+
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
		//Tamaño
		$('#sizeUpdate').val(this.updateClass.fields[index].properties.size);
		$('#sizeUpdate').on('input', function(){
			that.updateClass.fields[index].properties.size = $('#sizeUpdate').val();
		});
		//Obligatorio
		if(this.updateClass.fields[index].properties.required===true){
			$('#requiredUpdate').prop('checked', true);
		}else{
			$('#requiredUpdate').prop('checked', false);
		}
		$('#requiredUpdate').click(function(){
			if($('input#requiredUpdate:checked').length===1){
				that.updateClass.fields[index].properties.required = true;
			}else{
				that.updateClass.fields[index].properties.required = false;
			}
		});
		//Correo
		if(this.updateClass.fields[index].properties.email===true){
			$('#emailUpdate').prop('checked', true);
		}else{
			$('#emailUpdate').prop('checked', false);
		}
		$('#emailUpdate').click(function(){
			if($('input#emailUpdate:checked').length===1){
				that.updateClass.fields[index].properties.email = true;
			}else{
				that.updateClass.fields[index].properties.email = false;
			}
		});
		//URL
		if(this.updateClass.fields[index].properties.url===true){
			$('#urlUpdate').prop('checked', true);
		}else{
			$('#urlUpdate').prop('checked', false);
		}
		$('#urlUpdate').click(function(){
			if($('input#urlUpdate:checked').length===1){
				that.updateClass.fields[index].properties.url = true;
			}else{
				that.updateClass.fields[index].properties.url = false;
			}
		});
	}

}