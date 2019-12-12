import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions, ResponseContentType } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';

import { Properties } from './properties';

@Injectable()
export class PdfViewerServices{

	constructor (private _http:Http, private _properties:Properties){

	}

	getDocument(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('idDoc', id);
		headers.append('numVersion', '0');
		let options = new RequestOptions({ headers: headers, responseType: ResponseContentType.Blob });
		return this._http.get(this._properties.getDocument, options )
          .map(res => res.blob())
	}

	documentClass(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers});
		return this._http.get(this._properties.documentClass, options)
			.map(res => res.json());
	}

}