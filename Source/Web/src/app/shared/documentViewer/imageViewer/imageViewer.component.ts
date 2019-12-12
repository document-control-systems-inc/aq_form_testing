import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, Input } from '@angular/core';

import * as Chartist from 'chartist';

declare const $: any;

import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { Properties } from '../../../services/properties';

import { IMAGEVIEWER_CONFIG, ImageViewerConfig } from '@hallysonh/ngx-imageviewer';

const MY_IMAGEVIEWER_CONFIG: ImageViewerConfig = {
	tooltips: {
		enabled: false
	},
};

@Component({
	selector: 'imageViewer',
	templateUrl: './imageViewer.component.html',
	providers: [ PdfViewerServices, Properties, {
      provide: IMAGEVIEWER_CONFIG,
      useValue: MY_IMAGEVIEWER_CONFIG
    } ],
  	styleUrls: ['./imageViewer.component.css']
})

export class ImageViewerComponent implements AfterViewInit{

	//id de documento a mostrar
	@Input('idDoc') idDocumento;
	//Alto de visor
	public height;
	//Ancho de visor
	public width;
	//Imagen a mostrar
	public image = null;
	//id de componente
	@Input('id') id;

	constructor(private _pdfViewerServices:PdfViewerServices){

	}

	ngOnInit(){
		this.getImage();	
	}

	ngAfterViewInit(){
		//Visor de imagen responsivo
		let that = this;
		setTimeout(function(){
			that.width = $('#imgViewer'+that.id).width();
			if(that.width * 0.75 < 290) that.height = 290;
			else that.height = that.width * 0.75;
			$(window).on('resize', function(){
				setTimeout(function(){
					if($('#imgViewer'+that.id).width() != that.width){
						that.width = $('#imgViewer'+that.id).width();
						if(that.width * 0.75 < 290) that.height = 290;
						else that.height = that.width * 0.75;
					}
				},500);
			});
		},200);
	}

	//Función utilizada para obtener la imágen por idDocumento
	getImage(){
		this._pdfViewerServices.getDocument(localStorage.getItem('token'), this.idDocumento).subscribe(
			result => {
				let blob = new Blob([result], {type:'image/png'});
				var reader = new FileReader();
				reader.readAsDataURL(blob); 
				let that = this;
				reader.onloadend = function() {
					that.image = reader.result;         
				}
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

}