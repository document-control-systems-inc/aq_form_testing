import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Properties } from './properties';

@Injectable()
export class LoginServices{

	constructor (private _http:Http, private _properties:Properties){

	}

	login(user, password){
		let headers = new Headers();
		headers.append('user', user);
		headers.append('password', password);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.loginURL, options)
          .map(res => res.json());
	}

	logoutAll(user){
        let headers = new Headers();
        headers.append('user', user);
        let options = new RequestOptions({ headers: headers });
        return this._http.get(this._properties.logoutAll, options)
          .map(res => res.json());
    }
	
	recover(user, email){
		let headers = new Headers();
		headers.append('user', user);
		headers.append('mail', email);
		let options = new RequestOptions({ headers: headers});
		return this._http.get(this._properties.restorePassword, options)
			.map(res => res.json());
	}
	
	
}