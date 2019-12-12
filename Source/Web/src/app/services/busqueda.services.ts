import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Properties } from './properties';

@Injectable()
export class BusquedaServices{

	constructor (private _http:Http, private _properties:Properties){

	}

	executeSearch(token, search){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('search', search);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.search, options)
          .map(res => res.json());
	}

	searchType(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.searchType, options)
          .map(res => res.json());
	}

	getLstPath(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.getLstPath, options)
          .map(res => res.json());
	}

	getStoredSearch(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.storedSearch, options)
          .map(res => res.json());
	}

	setStoredSearch(token, search){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('search', JSON.stringify(search));
		let options = new RequestOptions({ headers: headers});
		return this._http.put(this._properties.storedSearch, {}, options)
          .map(res => res.json());
	}

}