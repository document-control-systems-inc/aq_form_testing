import { Component } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

import { Login } from '../../../entity/login';

@Component({
	selector: 'version-history',
	templateUrl: './versionHistory.component.html',
	styleUrls: ['./versionHistory.component.css'],
	providers: [  ]
})

export class VersionHistoryComponent{

	constructor(public _login:Login, private translate: TranslateService){	
		
	}

	ngOnInit(){

	}

}