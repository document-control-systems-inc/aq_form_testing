import { Injectable, NgZone } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

import { Router, ActivatedRoute } from '@angular/router';

import { Properties } from './properties';
import { LoginServices } from './login.services';

declare var $: any;
import swal from 'sweetalert2';

interface ICallback {
    ( error: Error, result?: boolean ) : void;
}

//Utilidades para notificaciones de error
@Injectable()
export class UtilitiesServices{
	
    //variables de manejo de sesiones
    public password;

    // Observable string sources
    private componentMethodCallSource = new Subject<any>();

    // Observable string streams
    componentMethodCalled$ = this.componentMethodCallSource.asObservable();

    // Service message commands
    callComponentMethod() {
        this.componentMethodCallSource.next();      
    }

	constructor (private _http:Http, private _properties:Properties, private _router:Router){

	}

	showSwalError(id, callback:ICallback){
        if(id===1||id===6||id===7||id===10){
            //localStorage.removeItem('token');
            $('body').addClass('sidebar-mini');
            let that = this;
            swal({
                title: 'Sesión expirada',
                html: '<div class="form-group">' +
                          '<p>Favor de volver a iniciar sesión.</p>' +
                          '<div class="card-content">' +
                                '<div class="input-group">' +
                                    '<span class="input-group-addon">' +
                                        '<i class="material-icons">face</i>' +
                                    '</span>' +
                                    '<div class="form-group label-floating">' +
                                        '<label class="control-label">User name</label>' + 
                                        '<input type="text" class="form-control" value="'+localStorage.getItem('domain')+'" disabled>' +
                                    '</div>' +
                                '</div>' +
                                '<div class="input-group">' +
                                    '<span class="input-group-addon">' + 
                                        '<i class="material-icons">lock_outline</i>' +
                                    '</span>' + 
                                    '<div class="form-group label-floating">' +
                                        '<label class="control-label">Password</label>' + 
                                        '<input type="password" class="form-control" id="password">' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                      /*'</div>' +
                      '<div class="footer text-center">' + 
                          '<button type="submit" class="btn btn-rose btn-simple btn-wd btn-lg" (click)="login()">Enter!</button>' +
                          '<button type="submit" class="btn btn-info btn-simple btn-wd btn-lg" (click)="redirectToLogin()">Cerrar sesión</button>' +*/
                      '</div>',
                type: 'error',
                showCancelButton: true,
                showConfirmButton: true,
                cancelButtonText: "Cerrar sesión",
                confirmButtonText: "Enter!",
                allowOutsideClick: false,
                allowEscapeKey: false
            }).then(function(){
                // function when confirm button clicked
                    that.password = $('#password').val();
                    that.login((error, result): void => {
                        if(result===true){
                            callback(null, true);
                        }                        
                    });
                }, function(dismiss){
                if(dismiss == 'cancel'){
                // function when cancel button is clicked
                    that.logoutAll();
                    that.redirectToLogin();
                    setTimeout(function(){
                        $('body').removeClass('sidebar-mini');
                    },1000);
                }                        
            });
        }/*else if(id===6){
            this.redirectToLogin();
        }*/
        else{
            this.notifyError("es_mx", id).subscribe(
                result => {
                    if(result.status===0){                    
                        swal({
                            type: 'error',
                            title: 'Error',
                            html: '<div class="form-group">' +
                                      '<p><strong>Error '+id+'. </strong> '+result.exito.message+' </p>' +
                                  '</div>',
                            buttonsStyling: false,
                            confirmButtonClass: 'btn btn-success'
                        });                  
                    }else{
                         this.showSwalError(result.status, null);
                    }
                }
            );
        }
    }

    /////////////////////////////////////////////////////////Bloque de login/////////////////////////////////////////////////////////////////

    login(callback:ICallback){
        this.getLogin(localStorage.getItem('domain'), this.password).subscribe(
            result => {
                if(result.status===0){
                    callback(null, true);
                    localStorage.setItem('token', result.exito);
                    //localStorage.setItem('domain', localStorage.getItem('domain'));
                    //this._login.language = this.language[0];
                    console.log("token en pantalla login:"+localStorage.getItem('token'));
                    $.notify({
                        icon: 'notifications',
                        message: 'Sesion iniciada correctamente.'
                    }, {
                        type: 'success',
                        timer: 500,
                        delay: 2000,
                        placement: {
                            from: 'top',
                            align: 'right'
                        }
                    });
                    $('body').removeClass('sidebar-mini');
                    this.callComponentMethod();
                }else if(result.status===3||result.status===105){
                    callback(null, false);
                    let msj;
                    if(result.status===3) msj = 'Contraseña incorrecta.';
                    else msj = 'Favor de ingresar contraseña.';
                    this.showSwalError(1, null);
                    $.notify({
                        icon: 'notifications',
                        message: msj
                    }, {
                        type: 'danger',
                        z_index: 103100,
                        timer: 500,
                        delay: 2000,
                        placement: {
                            from: 'top',
                            align: 'center'
                        }
                    });
                }else if(result.status===7 || result.status===10){
                    callback(null, false);
                    let that = this;
                    swal({
                        title: 'Número de sesiones superado',
                        html: '<div class="form-group">' +
                                  '<p>Es necesario cerrar todas las sesiones para continuar.</p>' +
                              '</div>',
                        type: 'warning',
                        showCancelButton: false,
                        showConfirmButton: true,
                        confirmButtonText: "Cerrar sesion",
                        allowOutsideClick: false,
                        allowEscapeKey: false
                    }).then(function(){
                        // function when confirm button clicked
                        that.logoutAll();
                        that.showSwalError(1, null);
                    });
                }else{
                    this.showSwalError(result.status, null);
                }
            },
            error => {
                var errorMessage = <any> error;
                console.log(errorMessage);
            }
        );
    }

    logoutAll(){
        this.getLogoutAll(localStorage.getItem('domain')).subscribe(
            result => {
                if(result.status===0){
                    $.notify({
                        icon: 'notifications',
                        message: 'Sesiones cerradas correctamente.'
                    }, {
                        type: 'success',
                        timer: 500,
                        delay: 2000,
                        placement: {
                            from: 'top',
                            align: 'right'
                        }
                    });
                }else{
                    this.showSwalError(result.status, null);
                }
            },
            error => {
                var errorMessage = <any> error;
                console.log(errorMessage);
            }
        );
    }

    getLogoutAll(user){
        let headers = new Headers();
        headers.append('user', user);
        let options = new RequestOptions({ headers: headers });
        return this._http.get(this._properties.logoutAll, options)
          .map(res => res.json());
    }

    getLogin(user, password){
        let headers = new Headers();
        headers.append('user', user);
        headers.append('password', password);
        let options = new RequestOptions({ headers: headers });
        return this._http.get(this._properties.loginURL, options)
          .map(res => res.json());
    }

    redirectToLogin(){
        localStorage.removeItem('token');
        localStorage.removeItem('domain');
        this._router.navigate(['/']);
    }

    /////////////////////////////////////////////////////////Bloque de login/////////////////////////////////////////////////////////////////

	notifyError(locale, id){
		let headers = new Headers();
		headers.append('locale', locale);
		headers.append('id', id);
		let options = new RequestOptions({ headers: headers });
		return this._http.get(this._properties.errorURL, options)
          .map(res => res.json());
	}
}