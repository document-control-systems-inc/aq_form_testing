import { Component } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';

import { NavbarServices } from './navbar.services';
import { Login } from '../../../entity/login';

@Component({
	selector: 'navbar',
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.css'],
	providers: [ NavbarServices ]
})

export class NavbarComponent{
	public token:string;
	public userDN;
	public lstUsers;
	closeResult: string;

	constructor(private _navbarServices:NavbarServices, public _login:Login, private _router:Router, private translate: TranslateService){
		this.token = this._login.token;
		//this.translate.use(this._login.language);
		setTimeout(function(){
			$("a.activePage").parent().addClass('activePage');
		},20);
	}

	ngOnInit(){

	}

	logout(){
		this._navbarServices.logout(this.token).subscribe(
			result => {
				if(result.status===0){
					this.redirigirAHome();
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
	
	redirigirAHome(){
		this._router.navigate(['/']);
	}

}