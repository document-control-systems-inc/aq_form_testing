import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class NavbarServices{

	constructor (private _http:Http){

	}

	getUsers(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers });
		return this._http.get('http://localhost:8080/aquarius/users/', options)
          .map(res => res.json());
	}

	logout(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers });
		return this._http.get('http://localhost:8080/aquarius/logout/', options)
          .map(res => res.json());
	}

}