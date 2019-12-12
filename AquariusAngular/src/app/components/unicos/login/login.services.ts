import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class LoginServices{

	constructor (private _http:Http){

	}

	login(user, password){
		let headers = new Headers();
		headers.append('user', user);
		headers.append('password', password);
		let options = new RequestOptions({ headers: headers });
		return this._http.get('http://localhost:8080/aquarius/login/', options)
          .map(res => res.json());
	}
}