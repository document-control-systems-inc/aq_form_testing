import { Component, Input } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

import { SocialServices } from '../../../services/social.services';
import { UtilitiesServices } from '../../../services/utilities.services';
import { Properties } from '../../../services/properties';

@Component({
	selector: 'comments',
	templateUrl: './comments.component.html',
	styleUrls: ['./comments.component.css'],
	providers: [ UtilitiesServices, SocialServices, Properties ]
})

export class CommentsComponent{

	public comentario = "";
	//id documento asociado
	@Input('idDoc') idDocumento;
	//Lista de comentarios
	public lstComentarios;
	//Número de comentarios
	public numComentarios;

	constructor(private translate: TranslateService, public _socialServices: SocialServices, public _utilitiesServices: UtilitiesServices){	
		
	}

	ngOnInit(){
		//Añade el autosize
		$("textarea").keyup(function(e) {
		    while($(this).outerHeight() < this.scrollHeight + parseFloat($(this).css("borderTopWidth")) + parseFloat($(this).css("borderBottomWidth"))) {
		        $(this).height($(this).height()+1);
		    };
		});
		//Llamado de servicio de obtención de comentarios
		this.getComment();
	}

	//Función utilizada para añadir un nuevo comentario
	setComment(){
		this._socialServices.setComment(localStorage.getItem('token'), this.idDocumento, this.comentario).subscribe(
			result => {
				if(result.status===0){
					this.getComment();
					this.comentario = "";
				}else{
					this._utilitiesServices.showSwalError(result.status, null);
				}
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	//Función utilizada para obtener el listado de comentarios asociados a un registro
	getComment(){
		this._socialServices.getComment(localStorage.getItem('token'), this.idDocumento).subscribe(
			result => {
				if(result.status===0){
					this.lstComentarios = result.exito.comments;
					this.numComentarios = result.exito.numComments;
				}else{
					this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                        	let that = this;
                            setTimeout(function(){
                                that.getComment();
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

	//Función genérica para convertir fecha de timestamp a date con formato específico
	convertDate(timestamp){
		let date = new Date(timestamp);
		let day = date.getDate();
		let month;
		if(date.getMonth()+1 < 10){
			month = '0' + (date.getMonth()+1).toString();
		}else{
			month = (date.getMonth()+1).toString();
		}
		let year = date.getFullYear();
		let hour = date.getHours();
		let min = date.getMinutes();
		return day + '/' + month + '/' + year + ' ' + hour + ':' + min;
	}

}