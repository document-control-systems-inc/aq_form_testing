import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions, ResponseContentType } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';

import { Properties } from './properties';

@Injectable()
export class SecurityServices{

	constructor (private _http:Http, private _properties:Properties){

	}

	getUser(token){
		let headers = new Headers();
        headers.append('token', token);
        let options = new RequestOptions({ headers: headers });
        return this._http.get(this._properties.getUsers, options)
          .map(res => res.json());
	}

	getProfile(token){
        let headers = new Headers();
        headers.append('token', token);
        let options = new RequestOptions({ headers: headers });
        return this._http.get(this._properties.addProfile, options)
          .map(res => res.json());
    }

	addProfile(token, name, elements){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('name', name);
		headers.append('elements', JSON.stringify(elements));
		let options = new RequestOptions({ headers: headers});
		return this._http.put(this._properties.addProfile, {}, options)
          .map(res => res.json());
	}

	addPage(token, name){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('name', name);
		let options = new RequestOptions({ headers: headers});
		return this._http.put(this._properties.setPage, {}, options)
          .map(res => res.json());
	}

	addComponent(token, idPage, name){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('idPage', idPage);
		headers.append('name', name);
		let options = new RequestOptions({ headers: headers});
		return this._http.put(this._properties.setComponent, {}, options)
          .map(res => res.json());
	}

	updatePage(token, name, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('name', name);
		headers.append('id', id);
		let options = new RequestOptions({ headers: headers});
		return this._http.put(this._properties.setPage, {}, options)
          .map(res => res.json());
	}

	updateComponent(token, idPage, name, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('idPage', idPage);
		headers.append('name', name);
		headers.append('id', id);
		let options = new RequestOptions({ headers: headers});
		return this._http.put(this._properties.setComponent, {}, options)
          .map(res => res.json());
	}

	getPage(token){
        let headers = new Headers();
        headers.append('token', token);
        let options = new RequestOptions({ headers: headers });
        return this._http.get(this._properties.setPage, options)
          .map(res => res.json());
    }

    getComponent(token, id){
        let headers = new Headers();
        headers.append('token', token);
        headers.append('id', id);
        let options = new RequestOptions({ headers: headers });
        return this._http.get(this._properties.setComponent, options)
          .map(res => res.json());
    }

}