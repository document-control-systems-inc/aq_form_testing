import { Component, Input } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

@Component({
	selector: 'version-history',
	templateUrl: './versionHistory.component.html',
	styleUrls: ['./versionHistory.component.css'],
	providers: [  ]
})

export class VersionHistoryComponent{

	//Información de historial de versiones
	@Input('historial') historial = null;
	
	constructor(private translate: TranslateService){	
		
	}

	ngOnInit(){

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