import { Component, Input } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

import { ExplorarServices } from '../../../services/explorar.services';
import { UtilitiesServices } from '../../../services/utilities.services';
import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { Properties } from '../../../services/properties';

declare const Tiff: any;

@Component({
	selector: 'tiff-editor',
	templateUrl: './tiffEditor.component.html',
	styleUrls: ['./tiffEditor.component.css'],
	providers: [ ExplorarServices, UtilitiesServices, PdfViewerServices, Properties ]
})

export class TiffEditorComponent{

	//Variable de loading
	public loading = false;
	
	//* BLOQUE DE RENDER *//
	//id de componente
	@Input('id') id;
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
	
	//* BLOQUE DE EDITOR *//
	//Variable de tipo de acción(rect, text, image)
	public action = '';
	//Variables correspondientes a canvas principal
	public canvas;
	public ctx;
	//Utilizados para localizar las posiciones para pintar los rectángulos
	public startX;
	public startY;
	public offsetX;
	public offsetY;
	public scrollX;
	public scrollY;
	//Variable utilizada para identificar si se mantiene o no el click
	public isDown = false;
	//Color de rectangulo
	public color = null;
	//Variable de control de transparencia
	public opacity = true;
	//medidas de rectangulo al final
	public width;
	public height;
	//Lista de tamaños disponibles en sellos
	public lstSize = [
		{
			nombre: 'Pequeño',
			size: 12
		},{
			nombre: 'Mediano',
			size: 24
		},{
			nombre: 'Grande',
			size: 48
		}
	];
	//Propiedades de sello
	public objComentario = {
		comentario: null,
		size: null
	};
	//Variable utilizada para manejo de muestra de comentarios
	public showText = false;
	//Variable utilizada para manejo de opciones de rectangulo
	public showRect = false;
	//Variable utilizada para manejo de imagenes
	public showImg = false;
	//Lista de sellos
	public lstStamp;
	//id de sello seleccionado a colocar
	public selectedStamp;
	//Preview de imagen
	public imgPreview = null;
	//Arreglo de manejo de cambios
	public lstCambios = [];
	//Historial de imágenes
	public lstImg = [];

	constructor(private translate: TranslateService, private _explorarServices:ExplorarServices, private _utilitiesServices:UtilitiesServices, private _pdfViewerServices:PdfViewerServices){	
		
	}

	ngOnInit(){
		this.getTiff();
	}

	/*////////////////////////////////////////////////////////////////////////////////////////////////// 
		BLOQUE DE RENDER
	*///////////////////////////////////////////////////////////////////////////////////////////////////

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
							let originalCanvas = canvas.toDataURL('image/png');
							canvas.removeAttribute('width');
							canvas.removeAttribute('height');
							canvas.setAttribute('width', $('#tiff'+that.id).width());
							canvas.setAttribute('height', $('#tiff'+that.id).width() * 0.75);
							canvas.setAttribute('style', 'border-radius: 4px;');
							canvas.setAttribute('id','canvas'+(i+1)+that.id);
							that.lstCanvas.push(canvas);
							that.lstCambios.push(0);
							that.lstImg.push([]);
							that.lstImg[i].push(originalCanvas);
							var preview = tiff.toCanvas();
							preview.setAttribute('style', 'width:100%; border-radius: 4px;');
							preview.setAttribute('id','preview'+(i+1)+that.id);	
							that.lstPreview.push(preview);
						}
						for(let i = 0; i < that.lstCanvas.length; i++){
							let image = new Image;
						    image.src = that.lstImg[i][0];
							image.onload = function(){
								let ctx = that.lstCanvas[i].getContext('2d');
								ctx.drawImage(image, 0, 0, image.width, image.height, 0, 0, that.lstCanvas[i].width, that.lstCanvas[i].height);
							};
						}
						that.setPreview();
						that.setTiffPage();
						setTimeout(function(){
							that.getLstStamp();
						},100);
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
		$('#preview'+this.id).empty();
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

	/*////////////////////////////////////////////////////////////////////////////////////////////////// 
		BLOQUE DE RENDER
	*///////////////////////////////////////////////////////////////////////////////////////////////////

	/*////////////////////////////////////////////////////////////////////////////////////////////////// 
		BLOQUE DE EDICIÓN
	*///////////////////////////////////////////////////////////////////////////////////////////////////

	handleMouseDown(event:any){
		event.preventDefault();
	    event.stopPropagation();
	    
	    this.canvas = <HTMLCanvasElement> document.getElementById('canvas'+(this.canvasIndex+1)+this.id);
		this.ctx = this.canvas.getContext("2d");
		
	    // save the starting x/y of the rectangle
	    this.startX = event.clientX - this.offsetX;
	    this.startY = event.clientY - this.offsetY;

	    if(this.action === 'rect' || this.action === 'image'){
		    // set a flag indicating the drag has begun
		    this.isDown = true;
	    }else if(this.action === 'text'){
	    	this.ctx.fillStyle = this.color;
	    	this.ctx.font = this.objComentario.size + 'px Georgia';
	    	this.ctx.fillText(this.objComentario.comentario, this.startX, this.startY + this.objComentario.size/2);
	    	let canvas = this.lstCanvas[this.canvasIndex];
			let editedImage = canvas.toDataURL('image/png');
			this.lstImg[this.canvasIndex].splice(this.lstCambios[this.canvasIndex]+1);
			this.lstImg[this.canvasIndex].push(editedImage);
	    	this.lstCambios[this.canvasIndex]++;
	    	let previewCanvas = this.lstPreview[this.canvasIndex];
	    	let previewCtx = previewCanvas.getContext('2d');
			let image = new Image;
			image.src = editedImage;
			let that = this;
			image.onload = function(){
				previewCtx.drawImage(image, 0, 0, image.width, image.height, 0, 0, that.lstPreview[that.canvasIndex].width, that.lstPreview[that.canvasIndex].height);
			}
	    	this.setPreview();
			this.setTiffPage();
			this.objComentario = {
				comentario: null,
				size: null
			};
			this.showText = false;
			this.action = '';
			$('#canvas'+(this.canvasIndex+1)+this.id).css({"cursor": "auto"});
	    }
		    
	}

	handleMouseMove(event:any) {
	    event.preventDefault();
	    event.stopPropagation();

	    // if we're not dragging, just return
	    if (!this.isDown) {
	        return;
	    }

	    // get the current mouse position
	    let mouseX = event.clientX - this.offsetX;
	    let mouseY = event.clientY - this.offsetY;

	    // Put your mousemove stuff here

	    // clear the canvas
	    //this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

	    // calculate the rectangle width/height based
	    // on starting vs current mouse position
	    this.width = mouseX - this.startX;
	    this.height = mouseY - this.startY;

	    // draw a new rect from the start position 
	    // to the current mouse position
	    //this.ctx.strokeRect(this.startX, this.startY, width, height);
	    /*if(this.opacity===false){
			this.ctx.fillRect(this.startX, this.startY, this.width, this.height);
		    this.lstCambios.splice(this.numCambios+1);
	    }*/
		    

	}

	handleMouseUp(event:any) {
	    event.preventDefault();
	    event.stopPropagation();
	    if(this.action === 'rect'){
	    	if(this.opacity===true){
		    	let objColor = this.color.split(')');
		    	this.ctx.fillStyle = objColor[0] + ', 0.5)';
		    }else{
		    	this.ctx.fillStyle = this.color;
		    }
		    this.ctx.fillRect(this.startX, this.startY, this.width, this.height);

		    let canvas = this.lstCanvas[this.canvasIndex];
			let editedImage = canvas.toDataURL('image/png');
			this.lstImg[this.canvasIndex].splice(this.lstCambios[this.canvasIndex]+1);
			this.lstImg[this.canvasIndex].push(editedImage);
	    	this.lstCambios[this.canvasIndex]++;
	    	let previewCanvas = this.lstPreview[this.canvasIndex];
	    	let previewCtx = previewCanvas.getContext('2d');
			let image = new Image;
			image.src = editedImage;
			let that = this;
			image.onload = function(){
				previewCtx.drawImage(image, 0, 0, image.width, image.height, 0, 0, that.lstPreview[that.canvasIndex].width, that.lstPreview[that.canvasIndex].height);
			}
	    	this.setPreview();
			this.setTiffPage();

		    // the drag is over, clear the dragging flag
		    this.isDown = false;
		    this.action = '';
		    $('#canvas'+(this.canvasIndex+1)+this.id).css({"cursor": "auto"});
		    this.showRect = false;
	    }else if(this.action === 'image'){
	    	let that = this;
	    	var i = new Image();
			i.src = this.imgPreview;
			i.onload = function() {
				that.ctx.drawImage(i, that.startX, that.startY, that.width, that.height);
				let canvas = that.lstCanvas[that.canvasIndex];
				let editedImage = canvas.toDataURL('image/png');
				that.lstImg[that.canvasIndex].splice(that.lstCambios[that.canvasIndex]+1);
				that.lstImg[that.canvasIndex].push(editedImage);
		    	that.lstCambios[that.canvasIndex]++;
		    	let previewCanvas = that.lstPreview[that.canvasIndex];
		    	let previewCtx = previewCanvas.getContext('2d');
				let image = new Image;
				image.src = editedImage;
				image.onload = function(){
					previewCtx.drawImage(image, 0, 0, image.width, image.height, 0, 0, that.lstPreview[that.canvasIndex].width, that.lstPreview[that.canvasIndex].height);
				}
		    	that.setPreview();
				that.setTiffPage();
				that.isDown = false;
				that.selectedStamp = null;
				that.imgPreview = null
			    that.action = '';
			    $('#canvas'+(that.canvasIndex+1)+that.id).css({"cursor": "auto"});
			    that.showImg = false;
			}
	    }
		    
	}

	handleMouseOut(event:any) {
	    event.preventDefault();
	    event.stopPropagation();
	    if(this.action === 'rect' || this.action === 'image'){
	    	this.isDown = false;
	    }
	}

	undo(){
		if(this.lstCambios[this.canvasIndex]>0){
			this.lstCambios[this.canvasIndex]--;
			let previewCanvas = this.lstPreview[this.canvasIndex];
	    	let previewCtx = previewCanvas.getContext('2d');

			let canvas = this.lstCanvas[this.canvasIndex];
			let ctx = canvas.getContext('2d');

			let image = new Image;
			image.src = this.lstImg[this.canvasIndex][this.lstCambios[this.canvasIndex]];
			let that = this;
			image.onload = function(){
				ctx.drawImage(image, 0, 0, image.width, image.height, 0, 0, that.lstCanvas[that.canvasIndex].width, that.lstCanvas[that.canvasIndex].height);
				previewCtx.drawImage(image, 0, 0, image.width, image.height, 0, 0, that.lstPreview[that.canvasIndex].width, that.lstPreview[that.canvasIndex].height);
			}
			this.setPreview();
			this.setTiffPage();
		}
	}

	redo(){
		if(this.lstCambios[this.canvasIndex]<this.lstImg[this.canvasIndex].length-1){
			this.lstCambios[this.canvasIndex]++;
			let previewCanvas = this.lstPreview[this.canvasIndex];
	    	let previewCtx = previewCanvas.getContext('2d');

			let canvas = this.lstCanvas[this.canvasIndex];
			let ctx = canvas.getContext('2d');

			let image = new Image;
			image.src = this.lstImg[this.canvasIndex][this.lstCambios[this.canvasIndex]];
			let that = this;
			image.onload = function(){
				ctx.drawImage(image, 0, 0, image.width, image.height, 0, 0, that.lstCanvas[that.canvasIndex].width, that.lstCanvas[that.canvasIndex].height);
				previewCtx.drawImage(image, 0, 0, image.width, image.height, 0, 0, that.lstPreview[that.canvasIndex].width, that.lstPreview[that.canvasIndex].height);
			}
			this.setPreview();
			this.setTiffPage();
		}
	}

	showTextOptions(){
		this.showText = true;
	}

	setText(){
		this.action = 'text';
		let canvasElement = $('#canvas'+(this.canvasIndex+1)+this.id);
		let canvasOffset = canvasElement.offset();
		this.offsetX = canvasOffset.left;
		this.offsetY = canvasOffset.top;
		this.scrollX = canvasElement.scrollLeft();
		this.scrollY = canvasElement.scrollTop();
		let that = this;
		// listen for mouse events
		$('#canvas'+(this.canvasIndex+1)+this.id).mousedown(function (e) {
		    that.handleMouseDown(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).mousemove(function (e) {
		    that.handleMouseMove(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).mouseup(function (e) {
		    that.handleMouseUp(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).mouseout(function (e) {
		    that.handleMouseOut(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).css({"cursor": "text"});
	}

	setRect(){
		this.showRect = true;
		this.action = 'rect';
		let canvasElement = $('#canvas'+(this.canvasIndex+1)+this.id);
		let canvasOffset = canvasElement.offset();
		this.offsetX = canvasOffset.left;
		this.offsetY = canvasOffset.top;
		this.scrollX = canvasElement.scrollLeft();
		this.scrollY = canvasElement.scrollTop();
		let that = this;
		// listen for mouse events
		$('#canvas'+(this.canvasIndex+1)+this.id).mousedown(function (e) {
		    that.handleMouseDown(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).mousemove(function (e) {
		    that.handleMouseMove(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).mouseup(function (e) {
		    that.handleMouseUp(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).mouseout(function (e) {
		    that.handleMouseOut(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).css({"cursor": "crosshair"});
	}

	setImageOptions(){
		this.showImg = true;
	}

	setImage(){
		this.action = 'image';
		let canvasElement = $('#canvas'+(this.canvasIndex+1)+this.id);
		let canvasOffset = canvasElement.offset();
		this.offsetX = canvasOffset.left;
		this.offsetY = canvasOffset.top;
		this.scrollX = canvasElement.scrollLeft();
		this.scrollY = canvasElement.scrollTop();
		let that = this;
		// listen for mouse events
		$('#canvas'+(this.canvasIndex+1)+this.id).mousedown(function (e) {
		    that.handleMouseDown(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).mousemove(function (e) {
		    that.handleMouseMove(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).mouseup(function (e) {
		    that.handleMouseUp(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).mouseout(function (e) {
		    that.handleMouseOut(e);
		});
		$('#canvas'+(this.canvasIndex+1)+this.id).css({"cursor": "crosshair"});
	}

	setPreviewEditor(){
		for(let i = 0; i < this.lstStamp.length; i++){
			if(this.lstStamp[i].id === this.selectedStamp){
				this.imgPreview = this.lstStamp[i].img;
				break;
			}
		}
	}

	//Función utilizada para obtener la lista de sellos
	getLstStamp(){
		this._explorarServices.getLstStamps(localStorage.getItem('token')).subscribe(
			result => {
				if(result.status===0){
					this.lstStamp = result.exito;
					for(let i = 0; i < this.lstStamp.length; i++){
						this.getStamp(this.lstStamp[i].id, i);
					}
					this.loading = false;
				}else{
					this.loading = false;
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
						if(result===true){
							let that = this;
							setTimeout(function(){
								that.getLstStamp();
							},1000);
						}                        
					});
				}
			},
			error => {
				this.loading = false;
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	getStamp(id, index){
		this._explorarServices.getStamp(localStorage.getItem('token'), id).subscribe(
			result => {
				let blob = new Blob([result], {type:'image/png'});
				var reader = new FileReader();
				reader.readAsDataURL(blob); 
				let that = this;
				reader.onloadend = function() {
					that.lstStamp[index].img = reader.result;      
				}
			},
			error => {
				this.loading = false;
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	/*////////////////////////////////////////////////////////////////////////////////////////////////// 
		BLOQUE DE EDICIÓN
	*///////////////////////////////////////////////////////////////////////////////////////////////////

}