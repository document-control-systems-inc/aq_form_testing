import { Component } from '@angular/core';
import { NgModel } from '@angular/forms';
import { NgClass } from '@angular/common';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';

import { DocumentViewer } from '../../../entity/documentViewer';
import { Login } from '../../../entity/login';

@Component({
	selector: 'documentViewer',
	templateUrl: './documentViewer.component.html',
	styleUrls: ['./documentViewer.component.css'],
	providers: [  ]
})

export class DocumentViewerComponent{
	//Nombre de pantalla actual
	public componentName:string;
	//Boolean utilizado para alternar entre pantalla de visor y editor de pdf
	public isEditor:boolean; 

	constructor(public _documentViewer:DocumentViewer, public _login:Login, private translate: TranslateService){
		this.translate.use(this._login.language);
		this._documentViewer.isEditor = false;
		this._documentViewer.componentName = "documentViewer.viewer";
	}

	hideEditor(){
		this._documentViewer.isEditor = false;
		this._documentViewer.componentName = "documentViewer.viewer";
	}

}