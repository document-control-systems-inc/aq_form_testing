import { Component, OnInit, Injectable } from '@angular/core';
import PerfectScrollbar from 'perfect-scrollbar';
import { NgModel } from '@angular/forms';

import { Router, ActivatedRoute } from '@angular/router';

/*import { SidebarServices } from '../services/sidebar.services';
import { LoginServices } from '../services/login.services';
import { UtilitiesServices } from '../services/utilities.services';
import { Properties } from '../services/properties';*/

declare var $: any;
declare var swal: any;

//Metadata
export interface RouteInfo {
    path: string;
    title: string;
    type: string;
    icontype: string;
    collapse?: string;
    children?: ChildrenItems[];
}

export interface ChildrenItems {
    path: string;
    title: string;
    ab: string;
    type?: string;
}

//Menu Items
export const ROUTES: RouteInfo[] = [
    {
        path: '/config',
        title: 'Configuración general',
        type: 'sub',
        icontype: 'settings_applications',
        collapse: 'config',
        children: [
            {path: 'ldap', title: 'Conexión LDAP', ab:'C'},
            {path: 'sessions', title: 'Manejo de sesiones', ab:'MS'},
            {path: 'mail', title: 'Correos electrónicos', ab:'CE'},
            {path: 'domain', title: 'Dominio', ab:'D'},
            {path: 'storage', title: 'Storage area', ab:'SA'},
            {path: 'multipart', title: 'Multipart', ab:'MP'},
            {path: 'component', title: 'Pantallas', ab:'PT'}
        ]
    },
    {
        path: '/database',
        title: 'Base de datos',
        type: 'link',
        icontype: 'storage'
    },
    {
        path: '/class',
        title: 'Clase documental',
        type: 'link',
        icontype: 'description'
    },
    {
        path: '/users',
        title: 'Usuarios',
        type: 'link',
        icontype: 'account_box'
    },
    {
        path: '/admins',
        title: 'Administradores',
        type: 'link',
        icontype: 'supervisor_account'
    },
    {
        path: '/groups',
        title: 'Grupos',
        type: 'link',
        icontype: 'group'
    },
    {
        path: '/process',
        title: 'Procesos',
        type: 'link',
        icontype: 'assignment'
    },
    {
        path: '/permisos',
        title: 'Permisos',
        type: 'link',
        icontype: 'face'
    }
];
/*export const ROUTES: RouteInfo[] = [{
        path: '/dashboard',
        title: 'Dashboard',
        type: 'link',
        icontype: 'dashboard'
    },{
        path: '/components',
        title: 'Components',
        type: 'sub',
        icontype: 'apps',
        collapse: 'components',
        children: [
            {path: 'buttons', title: 'Buttons', ab:'B'},
            {path: 'grid', title: 'Grid System', ab:'GS'},
            {path: 'panels', title: 'Panels', ab:'P'},
            {path: 'sweet-alert', title: 'Sweet Alert', ab:'SA'},
            {path: 'notifications', title: 'Notifications', ab:'N'},
            {path: 'icons', title: 'Icons', ab:'I'},
            {path: 'typography', title: 'Typography', ab:'T'}
        ]
    },{
        path: '/forms',
        title: 'Forms',
        type: 'sub',
        icontype: 'content_paste',
        collapse: 'forms',
        children: [
            {path: 'regular', title: 'Regular Forms', ab:'RF'},
            {path: 'extended', title: 'Extended Forms', ab:'EF'},
            {path: 'validation', title: 'Validation Forms', ab:'VF'},
            {path: 'wizard', title: 'Wizard', ab:'W'}
        ]
    },{
        path: '/tables',
        title: 'Tables',
        type: 'sub',
        icontype: 'grid_on',
        collapse: 'tables',
        children: [
            {path: 'regular', title: 'Regular Tables', ab:'RT'},
            {path: 'extended', title: 'Extended Tables', ab:'ET'},
            {path: 'datatables.net', title: 'Datatables.net', ab:'DT'}
        ]
    },{
        path: '/maps',
        title: 'Maps',
        type: 'sub',
        icontype: 'place',
        collapse: 'maps',
        children: [
            {path: 'google', title: 'Google Maps', ab:'GM'},
            {path: 'fullscreen', title: 'Full Screen Map', ab:'FSM'},
            {path: 'vector', title: 'Vector Map', ab:'VM'}
        ]
    },{
        path: '/widgets',
        title: 'Widgets',
        type: 'link',
        icontype: 'widgets'

    },{
        path: '/charts',
        title: 'Charts',
        type: 'link',
        icontype: 'timeline'

    },{
        path: '/calendar',
        title: 'Calendar',
        type: 'link',
        icontype: 'date_range'
    },{
        path: '/pages',
        title: 'Pages',
        type: 'sub',
        icontype: 'image',
        collapse: 'pages',
        children: [
            {path: 'pricing', title: 'Pricing', ab:'P'},
            {path: 'timeline', title: 'Timeline Page', ab:'TP'},
            {path: 'login', title: 'Login Page', ab:'LP'},
            {path: 'register', title: 'Register Page', ab:'RP'},
            {path: 'lock', title: 'Lock Screen Page', ab:'LSP'},
            {path: 'user', title: 'User Page', ab:'UP'}
        ]
    }
];*/

@Component({
    selector: 'app-sidebar-cmp',
    styleUrls: ['./sidebar.component.css'],
    templateUrl: 'sidebar.component.html',
    providers: [ /*LoginServices, SidebarServices, UtilitiesServices, Properties*/ ]
})

export class SidebarComponent implements OnInit {
    public menuItems: any[];
    public userName:string;
    public password:string;
    public userData: any = {
        "dn":null,
        "givenName":null,
        "lastName":null,
        "mail":null
    };
    public logged = false;
    public iconDefault: boolean = false;
    public userProfile: any = null;


    isMobileMenu() {
        if ($(window).width() > 991) {
            return false;
        }
        return true;
    };

    constructor(private _router:Router, /*private _utilitiesServices: UtilitiesServices,private _loginServices:LoginServices, private _sidebarServices:SidebarServices,*/ private route:ActivatedRoute){
        if(!localStorage.getItem('token')){
            this._router.navigate(['/']);
        }
        /*this._utilitiesServices.componentMethodCalled$.subscribe(
            () => {
                //setTimeout(function(){
                    this.getUserData();
                //},500);
            }           
        );*/
    }

    ngOnInit() {
        this.menuItems = ROUTES.filter(menuItem => menuItem);
        //this.getUserData();
    }

    /*logout(){
        this._sidebarServices.logout(localStorage.getItem('token')).subscribe(
            result => {
                if(result.status===0){
                    $.notify({
                        icon: 'info_outline',
                        message: 'Sesión cerrada correctamente.'
                    }, {
                        type: 'success',
                        timer: 500,
                        delay: 1000, 
                        placement: {
                            from: 'top',
                            align: 'right'
                        }
                    });
                    this.redirectToLogin();
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                            this.logout();
                        }                        
                    });
                }
            },
            error => {
                var errorMessage = <any> error;
                console.log(errorMessage);
            }
        );
    }

    getUserData(){
        this._sidebarServices.getUserData(localStorage.getItem('token'), localStorage.getItem('domain')).subscribe(
            result => {
                if(result.status===0){
                    this.userData.dn = result.exito.dn;
                    this.userData.givenName = result.exito.givenName;
                    this.userData.lastName = result.exito.lastName;
                    this.userData.mail = result.exito.mail;
                }else{
                    this._utilitiesServices.showSwalError(result.status, (error, result): void => {
                        if(result===true){
                            let that = this;
                            setTimeout(function(){
                                that.getUserData();
                            },1000);
                        }                        
                    });
                }
            },
            error => {
                var errorMessage = <any> error;
                console.log(errorMessage);
            }
        );
    }*/
    
    redirectTo(route){
        this._router.navigate([route]);
        location.reload();
    }

    redirectToLogin(){
        localStorage.removeItem('token');
        localStorage.removeItem('domain');
        this._router.navigate(['/']);
    }

    updatePS(): void  {
        if (window.matchMedia(`(min-width: 960px)`).matches && !this.isMac()) {
            const elemSidebar = <HTMLElement>document.querySelector('.sidebar .sidebar-wrapper');
            let ps = new PerfectScrollbar(elemSidebar, { wheelSpeed: 2, suppressScrollX: true });
        }
    }
    isMac(): boolean {
        let bool = false;
        if (navigator.platform.toUpperCase().indexOf('MAC') >= 0 || navigator.platform.toUpperCase().indexOf('IPAD') >= 0) {
            bool = true;
        }
        return bool;
    }
}
