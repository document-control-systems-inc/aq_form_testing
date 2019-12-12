import { Routes } from '@angular/router';

import { LdapComponent } from './ldap/ldap.component';
import { SessionComponent } from './session/session.component';
import { MailComponent } from './mail/mail.component';
import { DomainConfigComponent } from './domain/domain.component';
import { StorageComponent } from './storage/storage.component';
import { MultipartComponent } from './multipart/multipart.component';
import { PantallaComponent } from './pantalla/pantalla.component';

export const ConfigurationRoutes: Routes = [
    {
        path: '',
        children: [ {
            path: 'ldap',
            component: LdapComponent
        }]
    },
    {
        path: '',
        children: [ {
            path: 'sessions',
            component: SessionComponent
        }]
    },
    {
        path: '',
        children: [ {
            path: 'mail',
            component: MailComponent
        }]
    },
    {
        path: '',
        children: [ {
            path: 'domain',
            component: DomainConfigComponent
        }]
    },
    {
        path: '',
        children: [ {
            path: 'storage',
            component: StorageComponent
        }]
    },
    {
        path: '',
        children: [ {
            path: 'multipart',
            component: MultipartComponent
        }]
    },
    {
        path: '',
        children: [ {
            path: 'component',
            component: PantallaComponent
        }]
    }
];
