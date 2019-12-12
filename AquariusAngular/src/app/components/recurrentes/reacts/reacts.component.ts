import { Component } from '@angular/core';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute, Params } from '@angular/router';

import * as $ from 'jquery';

import { TranslateService } from '@ngx-translate/core';

import { Login } from '../../../entity/login';

@Component({
	selector: 'reacts',
	templateUrl: './reacts.component.html',
	styleUrls: ['./reacts.component.css'],
	providers: [  ]
})

export class ReactsComponent{

	public likeNumber:number;
	public rateNumber:number;
	public commentsNumber:number;
	public liked:boolean;
	public rated:boolean;

	constructor(public _login:Login, private translate: TranslateService){	
		this.likeNumber = 33;
		this.rateNumber = 0;
		this.commentsNumber = 3;
		this.liked = false;
		this.rated = false;
	}

	ngOnInit(){

	}

	like(){
		this.liked=!this.liked;
		if(this.liked){
			this.likeNumber++;
		}else{
			this.likeNumber--;
		}
	}

}