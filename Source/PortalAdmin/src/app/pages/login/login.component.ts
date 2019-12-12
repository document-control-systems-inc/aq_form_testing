import { Component, OnInit, ElementRef, Injectable } from '@angular/core';
import { NgClass } from '@angular/common';
import { NgModel } from '@angular/forms';
import { Router } from '@angular/router';

import { LoginServices } from '../../services/login.services';
import { Properties } from '../../services/properties';
import { UtilitiesServices} from '../../services/utilities.services';

declare var $: any;
import swal from 'sweetalert2';

@Component({
    selector: 'app-login-cmp',
    templateUrl: './login.component.html',
    providers: [ LoginServices, Properties, UtilitiesServices ]
})

@Injectable()
export class LoginComponent implements OnInit {
    test: Date = new Date();
    private toggleButton: any;
    private sidebarVisible: boolean;
    private nativeElement: Node;
    public isLogin: boolean;
    private value: String;

    public user:string;
    public password:string;
    public token:string;
    public mail:string;
    public recoverUser:string;
    //public language:any[];


    constructor(private element: ElementRef, private _loginServices:LoginServices, private _router:Router, private _utilitiesServices: UtilitiesServices) {
        this.nativeElement = element.nativeElement;
        this.sidebarVisible = false;
        this.isLogin = true;
    }

    ngOnInit() {
        var navbar : HTMLElement = this.element.nativeElement;
        this.toggleButton = navbar.getElementsByClassName('navbar-toggle')[0];

        setTimeout(function() {
            // after 1000 ms we add the class animated to the login/register card
            $('.card').removeClass('card-hidden');
        }, 700);
    }

    sidebarToggle() {
        var toggleButton = this.toggleButton;
        var body = document.getElementsByTagName('body')[0];
        var sidebar = document.getElementsByClassName('navbar-collapse')[0];
        if (this.sidebarVisible == false) {
            setTimeout(function() {
                toggleButton.classList.add('toggled');
            }, 500);
            body.classList.add('nav-open');
            this.sidebarVisible = true;
        } else {
            this.toggleButton.classList.remove('toggled');
            this.sidebarVisible = false;
            body.classList.remove('nav-open');
        }
    }

    login(){
        this._loginServices.login(this.user, this.password).subscribe(
            result => {
                if(result.status===0){
                    localStorage.setItem('token', result.exito);
                    localStorage.setItem('domain', this.user);
                    //localStorage.setItem('id', this.id);
                    //this._login.language = this.language[0];
                    console.log("token en pantalla login:"+localStorage.getItem('token') + "imprime usuario:"+localStorage.getItem('domain'));
                    this.redirectToHome();
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
                }else if(result.status===7){
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
                        that.logoutAll(that.user);
                    });
                }else{
                    this._utilitiesServices.showSwalError(result.status, null);
                }
            },
            error => {
                var errorMessage = <any> error;
                console.log(errorMessage);
            }
        );
    }

    logoutAll(user){
        this._loginServices.logoutAll(user).subscribe(
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
                    this._utilitiesServices.showSwalError(result.status, null);
                }
            },
            error => {
                var errorMessage = <any> error;
                console.log(errorMessage);
            }
        );
    }

    recoverPass(){
        this._loginServices.recover(this.recoverUser, this.mail).subscribe(
            result => {
                if(result.status=== 0){
                    this.showLogin();
                    //notificación
                    $.notify({
                        icon: 'notifications',
                        message: 'Se cambio correctamente la contraseña.'
                    }, {
                        type: 'info',
                        timer: 500,
                        delay: 2000,
                        placement: {
                            from: 'top',
                            align: 'right'
                        }
                    });
                }else{
                    this._utilitiesServices.showSwalError(result.status, null);
                }
            },
            error => {
                var errorMessage = <any> error;
                console.log(errorMessage);
            }
        );
    }

    redirectToHome(){
        this._router.navigate(['/database']);
    }

    showRecoverAccount(){
        this.isLogin = false;
        setTimeout(function() {
            // after 1000 ms we add the class animated to the login/register card
            $('.card').removeClass('card-hidden');
        }, 200);
    }

    showLogin(){
        this.isLogin = true;
        setTimeout(function() {
            // after 1000 ms we add the class animated to the login/register card
            $('.card').removeClass('card-hidden');
        }, 200);
    }

}
