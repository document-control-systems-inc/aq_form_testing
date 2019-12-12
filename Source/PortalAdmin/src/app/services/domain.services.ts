import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Properties } from './properties';

@Injectable()
export class DomainServices{

	constructor (private _http:Http, private _properties:Properties){

	}

	setDomain(domain, token){
		let headers = new Headers();
		headers.append('domain', domain);
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers });
		return this._http.put(this._properties.domain, {}, options)
          .map(res => res.json());
	}

	setSubdomain(domain, token, id){
		let headers = new Headers();
		headers.append('domain', domain);
		headers.append('token', token);
		headers.append('id', id);
		let options = new RequestOptions({ headers: headers });
		return this._http.put(this._properties.domain, {}, options)
          .map(res => res.json());
	}

	getDomain(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.domain, options)
          .map(res => res.json());
	}
	
	
}