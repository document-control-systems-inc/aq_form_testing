import { Component, OnInit, AfterViewInit, ViewChild, ElementRef, Input } from '@angular/core';

import * as Chartist from 'chartist';

declare const $: any;

import { PdfViewerServices } from '../../../services/pdfViewer.services';
import { Properties } from '../../../services/properties';

@Component({
	selector: 'videoPlayer',
	templateUrl: './videoPlayer.component.html',
	providers: [ PdfViewerServices, Properties ],
  	styleUrls: ['./videoPlayer.component.css']
})

export class VideoPlayerComponent implements AfterViewInit{

	//id de documento a mostrar
	@Input('idDoc') idDocumento;
	//Alto de visor
	public height;
	//Ancho de visor
	public width;
	//Video a mostrar
	public video = null;

	constructor(private _pdfViewerServices:PdfViewerServices){

	}

	ngOnInit(){
		this.getVideo();	
	}

	ngAfterViewInit(){
		//Reproductor de video responsivo
		let that = this;
		setTimeout(function(){
			that.width = $('#player').width();
			if(that.width * 0.75 < 290) that.height = 290;
			else that.height = that.width * 0.75;
			$(window).on('resize', function(){
				setTimeout(function(){
					if($('#player').width() != that.width){
						that.width = $('#player').width();
						if(that.width * 0.75 < 290) that.height = 290;
						else that.height = that.width * 0.75;
					}
				},500);
			});
		},1300);
	}

	//Función utilizada para obtener la imágen por idDocumento
	getVideo(){
		this._pdfViewerServices.getDocument(localStorage.getItem('token'), this.idDocumento).subscribe(
			result => {
				var reader = new FileReader();
				reader.readAsDataURL(result); 
				let that = this;
				reader.onloadend = function() {
					that.video = reader.result;
				}
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

}