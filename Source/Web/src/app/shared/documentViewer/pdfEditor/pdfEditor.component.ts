import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { NgModel } from '@angular/forms';
import { AfterViewInit } from '@angular/core';
import { ViewChild } from '@angular/core';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { Properties } from '../../../services/properties';

import * as $ from 'jquery';
declare var PDFTron:any;

@Component({
	selector: 'pdf-editor',
	templateUrl: './pdfEditor.component.html',
	styleUrls: ['./pdfEditor.component.css'],
	providers: [ PdfViewerServices, Properties ]
})

export class PdfEditorComponent implements OnChanges{

	//token de login
	public token:string;
	//pdf en base64
	public pdf:string;
	//xod exportado
	public exportedXOR:string;
	//id documento a mostrar
	@Input('idDoc') idDocumento;

	public myWebViewer;

	constructor(private _pdfViewerServices:PdfViewerServices){	
		this.token = localStorage.getItem('token');
	}

	ngOnInit(){
		this.getDocument();
	}

	ngOnChanges(changes: SimpleChanges){
		this.getDocumentAgain();
	}

	getDocumentAgain(){
		this._pdfViewerServices.getDocument(this.token, this.idDocumento).subscribe(
			result => {
				var blob = new Blob([result], {type:'application/pdf'});
				var url = URL.createObjectURL(blob);
				this.myWebViewer.loadDocument(url);
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	getDocument(){
		this._pdfViewerServices.getDocument(this.token, this.idDocumento).subscribe(
			result => {
				var blob = new Blob([result], {type:'application/pdf'});
				var url = URL.createObjectURL(blob);
				let that = this;
				$(function() {
					//Primero se identifica el elemento en el cual se pintará el editor
					var viewerElement = document.getElementById('viewer');
					//Inicializador de WebViewer
					that.myWebViewer = new PDFTron.WebViewer({
						type: "html5",
						//Ruta donde se encuentran las librerías de webviewer
						path: "../../../../../assets/WebViewer/lib",
						//PDF cargado desde backend
						initialDoc: url,
						//Tipo de documento a cargar
						documentType: "pdf",
						//Boolean utilizado para permitir subir documentos locales al editor
						showLocalFilePicker: false,
						//Boolean utilizado para permitir añadir anotaciones al PDF
						enableAnnotations: true,
						//Habilita las funciones del PDFNetJS
						pdfnet: true,
						//Licencia temporal
						l: "demo:gomado@gmail.com:7303aebd01b0c42d4cff2d74f4edaa4fe903baf8ac65540418"
					}, viewerElement);
				});
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

}