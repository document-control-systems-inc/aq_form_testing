import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Properties } from './properties';

@Injectable()
export class SidebarServices{

	constructor (private _http:Http, private _properties:Properties){

	}

	logout(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.logoutURL, options)
          .map(res => res.json());
	}

	getUserData(token, domain){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('domain', domain);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.getUserData, options)
          .map(res => res.json());
	}

}