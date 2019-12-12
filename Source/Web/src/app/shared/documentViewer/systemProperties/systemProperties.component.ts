import { Component, Input } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

@Component({
	selector: 'system-properties',
	templateUrl: './systemProperties.component.html',
	styleUrls: ['./systemProperties.component.css'],
	providers: [  ]
})

export class SystemPropertiesComponent{

	//Información de propiedades de sistema
	@Input('systemProperties') systemProperties = null;

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