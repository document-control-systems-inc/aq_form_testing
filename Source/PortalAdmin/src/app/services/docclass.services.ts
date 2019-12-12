import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions, ResponseContentType } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';

import { Properties } from './properties';

@Injectable()
export class DocClassServices{

	constructor (private _http:Http, private _properties:Properties){

	}

	documentClass(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers});
		return this._http.get(this._properties.documentClass, options)
			.map(res => res.json());
	}

}