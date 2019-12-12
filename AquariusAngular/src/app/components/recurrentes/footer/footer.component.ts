import { Component } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

import { Login } from '../../../entity/login';
import { DocumentViewer } from '../../../entity/documentViewer';

@Component({
	selector: 'footer',
	templateUrl: './footer.component.html',
	styleUrls: ['./footer.component.css'],
	providers: [  ]
})

export class FooterComponent{
	public languages:any[];
	constructor(public _login:Login, public _documentViewer:DocumentViewer, private translate: TranslateService){	
		
		this.languages = [
			{
				"value":"es",
				"language":"Español"
			},
			{
				"value":"en",
				"language":"English"
			}
		];

	}

	ngOnInit(){

	}

	changeLanguage(){
		this.translate.use(this._login.language);
	}

}