import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, Input } from '@angular/core';
import { Http, Request } from '@angular/http';

import * as Chartist from 'chartist';

declare const $: any;

import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { ExplorarServices } from '../../../services/explorar.services';
import { UtilitiesServices } from '../../../services/utilities.services';
import { Properties } from '../../../services/properties';

@Component({
	selector: 'image-editor',
	templateUrl: './imageEditor.component.html',
	providers: [ PdfViewerServices, ExplorarServices, UtilitiesServices, Properties ],
  	styleUrls: ['./imageEditor.component.css']
})

export class ImageEditorComponent{

	@Input('id') idDocumento;
	//Variable de loading
	public loading = false;
	//Variable de tipo de acción(rect, text, image)
	public action = '';
	//Variable de imagen de canvas
	public image;
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
	//Utilizados para manejo de historial en canvas
	public lstCambios = [];
	public numCambios = -1;
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

	ngOnInit(){
		setTimeout(() => {
			this.getImage();
		}, 800);
	}

	handleMouseDown(event:any){
		event.preventDefault();
	    event.stopPropagation();
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
	    	this.lstCambios.splice(this.numCambios+1);
		    let imageSource = this.canvas.toDataURL('image/png');
		    let image = new Image;
		    image.src = imageSource;
		    let that = this;
			image.onload = function(){
				that.lstCambios.push({
					image:image,
					imageSource:imageSource
				});
		    	that.numCambios++;
			};
			this.objComentario = {
				comentario: null,
				size: null
			};
			this.showText = false;
			this.action = '';
			$('#canvasEditor').css({"cursor": "auto"});
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
			this.lstCambios.splice(this.numCambios+1);
		    let imageSource = this.canvas.toDataURL('image/png');
		    let image = new Image;
		    image.src = imageSource;
		    let that = this;
			image.onload = function(){
				that.lstCambios.push({
					image:image,
					imageSource:imageSource
				});
		    	that.numCambios++;
			};
		    // the drag is over, clear the dragging flag
		    this.isDown = false;
		    this.action = '';
		    $('#canvasEditor').css({"cursor": "auto"});
		    this.showRect = false;
	    }else if(this.action === 'image'){
	    	let that = this;
	    	var i = new Image();
			i.src = this.imgPreview;
			i.onload = function() {
				that.ctx.drawImage(i, that.startX, that.startY, that.width, that.height);
				that.lstCambios.splice(that.numCambios+1);
			    let imageSource = that.canvas.toDataURL('image/png');
			    let image = new Image;
			    image.src = imageSource;
				image.onload = function(){
					that.lstCambios.push({
						image:image,
						imageSource:imageSource
					});
			    	that.numCambios++;
				};
				that.isDown = false;
				that.selectedStamp = null;
				that.imgPreview = null
			    that.action = '';
			    $('#canvasEditor').css({"cursor": "auto"});
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
		if(this.numCambios>0){
			this.numCambios--;
			this.ctx.drawImage(this.lstCambios[this.numCambios].image, 0, 0);
		}	
	}

	redo(){
		if(this.numCambios<this.lstCambios.length-1){
			this.numCambios++;
			this.ctx.drawImage(this.lstCambios[this.numCambios].image, 0, 0);
		}	
	}

	showTextOptions(){
		this.showText = true;
	}

	setText(){
		this.action = 'text';
		$('#canvasEditor').css({"cursor": "text"});
	}

	setRect(){
		this.showRect = true;
		this.action = 'rect';
		$('#canvasEditor').css({"cursor": "crosshair"});
	}

	setImageOptions(){
		this.showImg = true;
	}

	setImage(){
		this.action = 'image';
		$('#canvasEditor').css({"cursor": "crosshair"});
	}

	setPreview(){
		for(let i = 0; i < this.lstStamp.length; i++){
			if(this.lstStamp[i].id === this.selectedStamp){
				this.imgPreview = this.lstStamp[i].img;
				break;
			}
		}
	}

	//Función utilizada para obtener la imágen por idDocumento
	getImage(){
		this.loading = true;
		this._pdfViewerServices.getDocument(localStorage.getItem('token'), this.idDocumento).subscribe(
			result => {
				let blob = new Blob([result], {type:'image/png'});
				var reader = new FileReader();
				reader.readAsDataURL(blob); 
				let that = this;
				reader.onloadend = function() {
					that.image = reader.result;
					that.canvas = <HTMLCanvasElement> document.getElementById("canvasEditor");

					that.ctx = that.canvas.getContext("2d");
					var i = new Image();
					i.src = that.image;
					i.onload = function() {
						that.canvas.width = $('#editorContainer').width();
						if(that.canvas.width * 0.75 < 290) that.canvas.height = 290;
						else that.canvas.height = that.canvas.width * 0.75;

						that.ctx.drawImage(i, 0, 0, i.width, i.height, 0, 0, that.canvas.width, that.canvas.height);

						let imageSource = that.canvas.toDataURL('image/png');
					    let image = new Image;
					    image.src = imageSource;
						image.onload = function(){
							that.lstCambios.push({
								image:image,
								imageSource:imageSource
							});
					    	that.numCambios++;
						};
						
						let canvasElement = $('#canvasEditor');
						let canvasOffset = canvasElement.offset();
						that.offsetX = canvasOffset.left;
						that.offsetY = canvasOffset.top;
						that.scrollX = canvasElement.scrollLeft();
						that.scrollY = canvasElement.scrollTop();

						// listen for mouse events
						$("#canvasEditor").mousedown(function (e) {
						    that.handleMouseDown(e);
						});
						$("#canvasEditor").mousemove(function (e) {
						    that.handleMouseMove(e);
						});
						$("#canvasEditor").mouseup(function (e) {
						    that.handleMouseUp(e);
						});
						$("#canvasEditor").mouseout(function (e) {
						    that.handleMouseOut(e);
						});
					};
					setTimeout(function(){
						that.getLstStamp();
					},100);
				}
			},
			error => {
				this.loading = false;
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
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

	constructor(private _utilitiesServices:UtilitiesServices, private _pdfViewerServices:PdfViewerServices, private _explorarServices:ExplorarServices, public http:Http){

	}

}