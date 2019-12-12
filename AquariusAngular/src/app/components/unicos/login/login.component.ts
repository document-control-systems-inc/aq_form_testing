import { Component } from '@angular/core';
import { NgModel } from '@angular/forms';
import { Injectable } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { LoginServices } from './login.services';
import { Login } from '../../../entity/login';

import { TranslateService } from '@ngx-translate/core';

@Component({
	selector: 'login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css'],
	providers: [LoginServices]
})


@Injectable()
export class LoginComponent{
	
	public user:string;
	public password:string;
	public token:string;
	public isLogin:boolean;
	public language:any[];

	constructor(private _loginServices:LoginServices, private _router:Router, public _login : Login, private translate: TranslateService){
		this.isLogin = true;
		this.language = navigator.language.split("-"); 
		translate.setDefaultLang(this.language[0]);
	}

	login(){
		this._loginServices.login(this.user, this.password).subscribe(
			result => {
				if(result.status===0){
					this.setToken(result.exito);
					this._login.token = this.getToken();
					this._login.language = this.language[0];
					console.log("token en pantalla login:"+this.token);
					this.redirigirANavbar();
				}else{
					console.log("error "+result.status);
				}
			},
			error => {
				var errorMessage = <any> error;
				console.log(errorMessage);
			}
		);
	}

	setToken(token){
		this.token = token;
	}

	getToken(){
		return this.token;
	}

	showRecoverAccount(){
		this.isLogin = false;
	}

	showLogin(){
		this.isLogin = true;
	}

	redirigirANavbar(){
		this._router.navigate(['/documentViewer']);
	}

	switchLanguage(language: string) {
		this.translate.use(language);
	}


}