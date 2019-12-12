import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Properties } from './properties';

@Injectable()
export class SocialServices{

	constructor (private _http:Http, private _properties:Properties){

	}

	setComment(token, id, comment){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		headers.append('comment', comment);
		let options = new RequestOptions({ headers: headers });
		return this._http.put(this._properties.comment, JSON.stringify({}), options)
          .map(res => res.json());
	}

	getComment(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.comment, options)
          .map(res => res.json());
	}

}