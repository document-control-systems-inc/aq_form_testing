import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions, ResponseContentType } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class PdfViewerServices{

	constructor (private _http:Http){

	}

	getDocument(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		let options = new RequestOptions({ headers: headers, responseType: ResponseContentType.Blob });
		return this._http.get('http://localhost:8080/aquarius/document/content', options, )
          .map(res => res.blob())
	}

}