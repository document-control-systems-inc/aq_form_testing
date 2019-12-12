import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MaterialModule } from '../../app.module';

import { NgxTreeSelectModule } from 'ngx-tree-select';

import { LdapComponent } from './ldap/ldap.component';
import { SessionComponent } from './session/session.component';
import { MailComponent } from './mail/mail.component';
import { DomainConfigComponent } from './domain/domain.component';
import { StorageComponent } from './storage/storage.component';
import { MultipartComponent } from './multipart/multipart.component';
import { PantallaComponent } from './pantalla/pantalla.component';
import { ConfigurationRoutes } from './configuration.routing';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(ConfigurationRoutes),
    FormsModule,
    MaterialModule,
    ReactiveFormsModule,
    NgxTreeSelectModule.forRoot({
      expandMode: "None"
    })
  ],
  declarations: [
      LdapComponent,
      SessionComponent,
      MailComponent,
      DomainConfigComponent,
      StorageComponent,
      MultipartComponent,
      PantallaComponent
  ]
})

export class ConfigurationModule {}