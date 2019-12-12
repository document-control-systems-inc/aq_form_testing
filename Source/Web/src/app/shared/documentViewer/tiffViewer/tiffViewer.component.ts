import { Component, Input } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { Properties } from '../../../services/properties';

declare const Tiff: any;

@Component({
	selector: 'tiff',
	templateUrl: './tiffViewer.component.html',
	styleUrls: ['./tiffViewer.component.css'],
	providers: [ PdfViewerServices, Properties ]
})

export class TiffViewerComponent{

	//id de documento a mostrar
	@Input('idDoc') idDocumento;
	//lista de canvas para vista
	public lstCanvas = [];
	//lista de canvas para preview
	public lstPreview = [];
	//indice de canvas
	public canvasIndex:number = 0;
	//variable de front para paginado
	public page:any = 1;
	//número de páginas
	public numPages;
	//page height
	public canvasHeight;
	//id de componente
	@Input('id') id;

	constructor(private translate: TranslateService, private _pdfViewerServices:PdfViewerServices){	
		
	}

	ngOnInit(){
		this.getTiff();
	}

	//Función utilizada para obtener un tiff desde servidor
	getTiff(){
		this._pdfViewerServices.getDocument(localStorage.getItem('token'), this.idDocumento).subscribe(
			result => {
				this.lstCanvas = [];
				var reader = new FileReader();
				reader.readAsDataURL(result); 
				let that = this;
				reader.onloadend = function() {
					let xhr = new XMLHttpRequest();
					xhr.open('GET', reader.result);
					xhr.responseType = 'arraybuffer';
					xhr.onload = function (e) {
						var buffer = xhr.response;
						var tiff = new Tiff({buffer: buffer});
						var width = tiff.width();
        				var height = tiff.height();
        				that.numPages = tiff.countDirectory();
						for (var i = 0, len = tiff.countDirectory(); i < len; ++i) {
							tiff.setDirectory(i);
							var canvas = tiff.toCanvas();
							canvas.setAttribute('style', 'width:100%; border-radius: 4px;');
							canvas.setAttribute('id','canvas'+(i+1)+that.id);
							that.lstCanvas.push(canvas);
							var preview = tiff.toCanvas();
							preview.setAttribute('style', 'width:100%; border-radius: 4px;');
							preview.setAttribute('id','preview'+(i+1)+that.id);	
							that.lstPreview.push(preview);	
						}
						that.setPreview();
						that.setTiffPage();
					};
					xhr.send(); 
				}				 		
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	//Función utilizada para cambiar manualmente de página
	changePage(){
		if(this.page>0 && this.page<=this.numPages){
			this.canvasIndex = this.page - 1;
			this.setTiffPage();
		}
	}

	//Función utilizada para pintar una página de tiff
	setTiffPage(){
		$('#tiff'+this.id).empty();
		$('#tiff'+this.id).html(this.lstCanvas[this.canvasIndex]);
		this.page = this.canvasIndex + 1;	
	}

	//Función utilizada para pintar la vista previa
	setPreview(){
		let that = this;
		for(let i = 0; i < this.lstCanvas.length; i++){
			$('#preview'+this.id).append('<label>'+(i+1)+'</label>');
			$('#preview'+this.id).append(this.lstPreview[i]);
			$(document).on('click touchstart', '#preview'+(i+1)+this.id, function(){
				that.canvasIndex = i;
				that.setTiffPage();
			});
		}
		setTimeout(function(){
			that.canvasHeight =  $('#canvas1'+that.id).height();
			$('#preview'+that.id).css('max-height', that.canvasHeight);
		},200);
	}

	//Función utilizada para navegar a la página anterior
	prevPage(){
		if(this.canvasIndex>0){
			this.canvasIndex = this.canvasIndex - 1;
			this.setTiffPage();
		}	
	}

	//Función utilizada para navegar a la página anterior
	nextPage(){
		if(this.canvasIndex<this.lstCanvas.length-1){
			this.canvasIndex = this.canvasIndex + 1;
			this.setTiffPage();
		}	
	}

}