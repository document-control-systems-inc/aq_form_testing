import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions, ResponseContentType } from '@angular/http';
import { HttpClient, HttpRequest, HttpHeaders } from '@angular/common/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';
import { Observable } from 'rxjs/Observable';
import { Properties } from './properties';
import { TreeModel, NodeEvent, NodeDestructiveEvent, NodeCollapsedEvent } from 'ng2-tree';
import { Subject } from 'rxjs/Subject';


@Injectable()
export class ExplorarServices{

	constructor (private _http:Http, private _properties:Properties, private _httpClient: HttpClient){

	}

	setDocumentByIdParent(token, idParent, domain, documentclass, properties, file){
		let formData = new FormData();
		formData.append('file', file);
		formData.append('token', token);
		formData.append('idParent', idParent);
		formData.append('domain', domain);
		formData.append('documentclass', documentclass);
		formData.append('properties', JSON.stringify(properties));
		return this._http.post(this._properties.setDocumentByIdParent, formData)
        	.map(res => res.json());
	}

	getThumbnail(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('idDoc', id);
		let options = new RequestOptions({ headers: headers, responseType: ResponseContentType.Blob });
		return this._http.get(this._properties.thumbnail, options )
          .map(res => res.blob())
	}
	
	getDomain(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers});
		return this._http.get(this._properties.domainURL, options)
			.map(res => res.json());
	}

	getParentFolder(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		let options = new RequestOptions({ headers: headers});
		return this._http.get(this._properties.nodeInfo, options)
			.map(res => res.json());
	}

	getFolder(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.folderURL, options)
          .map(res => res.json());
	}
	
	getFolderContent(token, domain, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('domain', domain);
		headers.append('id', id);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.folderContentURL, options)
          .map(res => res.json());
	}

	setFolder(token, domain, name, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('domain', domain);
		headers.append('name', name);
		headers.append('id', id);
		let options = new RequestOptions({headers: headers});
		return this._http.put(this._properties.setFolderURL, JSON.stringify({}), options)
			.map(res => res.json());
	}	

	share(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		let options = new RequestOptions({headers: headers});
		return this._http.put(this._properties.share, JSON.stringify({}), options)
			.map(res => res.json());
	}

	getDocMetadata(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		let options = new RequestOptions({headers: headers});
		return this._http.get(this._properties.docMetadata, options)
        	.map(res => res.json());
	}

	getHistorial(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		let options = new RequestOptions({headers: headers});
		return this._http.get(this._properties.docVersion, options)
        	.map(res => res.json());
	}

	getSystemProperties(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		let options = new RequestOptions({headers: headers});
		return this._http.get(this._properties.systemProp, options)
        	.map(res => res.json());
	}

	getLstStamps(token){
		let headers = new Headers();
		headers.append('token', token);
		let options = new RequestOptions({headers: headers});
		return this._http.get(this._properties.lstStamp, options)
        	.map(res => res.json());
	}

	getStamp(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		let options = new RequestOptions({ headers: headers, responseType: ResponseContentType.Blob });
		return this._http.get(this._properties.stamp, options)
        	.map(res => res.blob());
	}

	deleteNode(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		headers.append('trash', 'true');
		let options = new RequestOptions({headers: headers});
		return this._http.delete(this._properties.nodeInfo, options)
			.map(res => res.json());
	}

	changeName(token, id, name){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		headers.append('name', name);
		let options = new RequestOptions({headers: headers});
		return this._http.put(this._properties.nodeInfo, JSON.stringify({}), options)
			.map(res => res.json());
	}

	setTemplate(token, id, name){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		headers.append('name', name);
		let options = new RequestOptions({headers: headers});
		return this._http.put(this._properties.template, JSON.stringify({}), options)
			.map(res => res.json());
	}

	setFavorites(token, id){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		let options = new RequestOptions({headers: headers});
		return this._http.put(this._properties.favorites, JSON.stringify({}), options)
			.map(res => res.json());
	}

	copyElement(token, id, idParent){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		headers.append('idParent', idParent);
		let options = new RequestOptions({headers: headers});
		return this._http.put(this._properties.copy, JSON.stringify({}), options)
			.map(res => res.json());
	}

	moveElement(token, id, idParent){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		headers.append('idParent', idParent);
		let options = new RequestOptions({headers: headers});
		return this._http.put(this._properties.move, JSON.stringify({}), options)
			.map(res => res.json());
	}

	download(token, id, type){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		headers.append('type', type);
		let options = new RequestOptions({ headers: headers, responseType: ResponseContentType.Blob });
		return this._http.get(this._properties.download, options)
        	.map(res => res.blob());
	}

	sendEmail(token, id, email, type){
		let headers = new Headers();
		headers.append('token', token);
		headers.append('id', id);
		headers.append('email', email);
		headers.append('type', type);
		let options = new RequestOptions({headers: headers});
		return this._http.get(this._properties.email, options)
        	.map(res => res.json());
	}

}
