import { Component, OnInit, ElementRef, Input } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

import { DateAdapter } from '@angular/material';

import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { UtilitiesServices} from '../../../services/utilities.services';
import { Properties } from '../../../services/properties';

@Component({
	selector: 'document-properties',
	templateUrl: './documentProperties.component.html',
	styleUrls: ['./documentProperties.component.css'],
	providers: [ PdfViewerServices, UtilitiesServices, Properties ]
})

export class DocumentPropertiesComponent implements OnInit{
	//Clase documental seleccionada
	public claseDocumental;
	//Listado de clases documentales
	public lstClase;
	//Metadata de documento seleccionado
	@Input('metadata') metadata = null;
	//Campos pertenecientes a clase documental
	public fields;
	

	constructor(private _pdfViewerServices:PdfViewerServices, private translate: TranslateService, private _utilitiesServices: UtilitiesServices){	
		
	}

	ngOnInit(){
		this.getDocument();
	}

	getDocument(){
		this._pdfViewerServices.documentClass(localStorage.getItem('token')).subscribe(
			result => {
			    if(result.status===0){
			    	this.lstClase = result.exito;
			    	for(let i = 0; i < this.lstClase.length; i++){
						if(this.lstClase[i].id===this.metadata[0].documentClass){
							this.claseDocumental = this.lstClase[i];
						}
					}
					this.setDocClass();
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

	setDocClass(){
		this.fields = this.claseDocumental.fields;
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