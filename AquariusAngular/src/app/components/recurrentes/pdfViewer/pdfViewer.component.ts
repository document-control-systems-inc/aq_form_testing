import { Component } from '@angular/core';
import { NgModel } from '@angular/forms';
import { AfterViewInit } from '@angular/core';
import { ViewChild } from '@angular/core';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { PdfViewerServices } from './pdfViewer.services';
import { Login } from '../../../entity/login';
import { DocumentViewer } from '../../../entity/documentViewer';

import { TranslateService } from '@ngx-translate/core';

@Component({
	selector: 'pdf',
	templateUrl: './pdfViewer.component.html',
	styleUrls: ['./pdfViewer.component.css'],
	providers: [ PdfViewerServices ]
})

export class PdfComponent{
	//token de login
	public token:string;
	//pdf en base64
	public pdf:string;
	//página actual
	public page:number;
	//variable temporal para mostrar página en pantalla
	public pageTemp;
	//número total de páginas
	public totalPage:number;
	//catálogo de porcentajes de zoom
	public zoomPercentages:any[];
	//zoom aplicado a pdf
	public zoom:number;
	//array de número de páginas utilizado en el panel lateral de vista previa
	public pagesArray:number[];
	//variable que almacena las clases pertenecientes al visor de pdf
	public pdfViewerClass:string;
	//variable que define si se muestra o no el panel lateral de vista previa
	public showPreviewPanel:boolean;
	//ícono de botón de mostrar/ocultar panel lateral
	public showPreviewPanelIcon:string;
	//identificador de tipo de viewer
	public fullscreen:boolean;
	//variable utilizada para definir las clases del visor sin pantalla completa
	public withoutFullscreenStyle:string;
	
	@ViewChild('prevPage') focus: any;

	constructor(private _pdfViewerServices:PdfViewerServices, public _login:Login, public _documentViewer:DocumentViewer, private translate: TranslateService){	
		this.token = this._login.token;
		this.zoom = 1;
		this.pdfViewerClass = "col-xs-12 col-sm-8 col-lg-10 text-left viewer";
		this.showPreviewPanelIcon = "fa fa-backward fa-2x";
		this.showPreviewPanel = true;
		this.fullscreen = false;
		this.withoutFullscreenStyle = "col-xs-12 withoutFullscreenViewer";
		this.zoomPercentages = [
			{
				"value":0.1,
				"percentage":"10%"
			},
			{
				"value":0.25,
				"percentage":"25%"
			},
			{
				"value":0.5,
				"percentage":"50%"
			},
			{
				"value":0.75,
				"percentage":"75%"
			},
			{
				"value":1,
				"percentage":"100%"
			},
			{
				"value":1.25,
				"percentage":"125%"
			},
			{
				"value":1.5,
				"percentage":"150%"
			},
			{
				"value":2,
				"percentage":"200%"
			},
			{
				"value":4,
				"percentage":"400%"
			}
		];
	}

	ngOnInit(){
		this.getDocument();
	}

	ngAfterViewInit() {
		this.focus.nativeElement.focus();
	}

	getDocument(){
		this._pdfViewerServices.getDocument(this.token, "pdf").subscribe(
			result => {
			    var reader = new FileReader();
				reader.readAsDataURL(result); 
				reader.onloadend = (event:any) => {
					this.pdf = reader.result;
				}
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}
		
	callBackFn(pdf: PDFDocumentProxy) {
		this.page = 1;
		this.pageTemp = 1;
		this.totalPage = pdf.numPages;
		this.pagesArray = this.getArrayPages(this.totalPage);
		setTimeout(function(){
			$('#preview').show();
		},5000);
	}

	getArrayPages = function(num) {
		let array = new Array(num);
		for (var i = 0; i < this.totalPage ; i++) {
			array[i] = i+1;
		}
	    return array;   
	}

	changePage(){
		if(this.pageTemp!==""){
			this.page = this.pageTemp;
		}
	}

	changePagePreview(page){
		this.pageTemp = page;
		this.page = page;
	}

	prePage(){
		if(this.pageTemp>1 && this.pageTemp <= this.totalPage){
			this.page--;
			this.pageTemp--;
		}		
	}

	nextPage(){
		if(this.pageTemp>=1 && this.pageTemp < this.totalPage){
			this.page++;
			this.pageTemp++;
		}
	}

	zoomIn(){
		if(this.zoom >= 0.1 && this.zoom < 4){
			if(this.zoom === 0.1) this.zoom = 0.25;
			else if(this.zoom === 0.25) this.zoom = 0.5;
			else if(this.zoom === 0.5) this.zoom = 0.75;
			else if(this.zoom === 0.75) this.zoom = 1;
			else if(this.zoom === 1) this.zoom = 1.25;
			else if(this.zoom === 1.25) this.zoom = 1.5;
			else if(this.zoom === 1.5) this.zoom = 2;
			else if(this.zoom === 2) this.zoom = 4;
		}
	}

	zoomOut(){
		if(this.zoom > 0.1 && this.zoom <= 4){
			if(this.zoom === 4) this.zoom = 2;
			else if(this.zoom === 2) this.zoom = 1.5;
			else if(this.zoom === 1.5) this.zoom = 1.25;
			else if(this.zoom === 1.25) this.zoom = 1;
			else if(this.zoom === 1) this.zoom = 0.75;
			else if(this.zoom === 0.75) this.zoom = 0.5;
			else if(this.zoom === 0.5) this.zoom = 0.25;
			else if(this.zoom === 0.25) this.zoom = 0.1;
		}
	}

	hidePreviewPanel(){
		if(this.showPreviewPanel){
			this.showPreviewPanel = !this.showPreviewPanel;
			this.pdfViewerClass = "col-xs-12 col-sm-12 col-lg-12 text-left viewer";
			this.showPreviewPanelIcon = "fa fa-forward fa-2x";
		} 
		else{
			this.showPreviewPanel = !this.showPreviewPanel;
			this.pdfViewerClass = "col-xs-12 col-sm-8 col-lg-10 text-left viewer";
			this.showPreviewPanelIcon = "fa fa-backward fa-2x";
		} 
	}

	openPdf() {
		this.pdfViewerClass = "col-xs-12 text-right overlay";
		this.withoutFullscreenStyle = "col-xs-12";
		this.zoom = 1;
		this.fullscreen = true;
		$('body').css('overflow', 'hidden');
	}

	closePdf() {
		if(this.showPreviewPanel){
			this.pdfViewerClass = "col-xs-12 col-sm-8 col-lg-10 text-left viewer";
			this.withoutFullscreenStyle = "col-xs-12 withoutFullscreenViewer";
		} 
	    else{
	    	this.pdfViewerClass = "col-xs-12 col-sm-12 col-lg-12 text-left viewer";
	    	this.withoutFullscreenStyle = "col-xs-12 withoutFullscreenViewer";
	    } 
	    this.fullscreen = false;
	    $('body').css('overflow', 'unset');
	}

	showEditor(){
		this._documentViewer.isEditor = true;
		this._documentViewer.componentName = "documentViewer.editor";
	}

}