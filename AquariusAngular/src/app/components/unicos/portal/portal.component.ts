import { Component } from '@angular/core';
import { NgModel } from '@angular/forms';
import { ViewChild } from '@angular/core';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { PortalServices } from './portal.services';
import { Login } from '../../../entity/login';

@Component({
	selector: 'portal',
	templateUrl: './portal.component.html',
	styleUrls: ['./portal.component.css'],
	providers: [ PortalServices ]
})

export class PortalComponent{
	public token:string;

	
	constructor(private _portalServices:PortalServices, public _login:Login){	
		this.token = this._login.token;
	}

	ngOnInit(){
	
	}

}