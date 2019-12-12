import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { DomSanitizer } from '@angular/platform-browser';
// import { MdIconModule, MdCardModule, MdInputModule, MdCheckboxModule, MdButtonModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { FlexLayoutModule } from '@angular/flex-layout';

import { PagesRoutes } from './pages.routing';

import { RegisterComponent } from './register/register.component';
import { PricingComponent } from './pricing/pricing.component';
import { LockComponent } from './lock/lock.component';
import { LoginComponent } from './login/login.component';
import { PortalComponent } from './portal/portal.component';
import { ResultadoBusquedaModule } from '../shared/busqueda/resultadoBusqueda/resultadoBusqueda.module';
import { FolderViewerModule } from '../shared/busqueda/folderViewer/folderViewer.module';
import { DocumentViewerModule } from './explorar/documentViewer/documentViewer.module';

//import { ExplorarComponent } from './explorar/explorar/explorar.component';
import { AngularCropperjsModule } from 'angular-cropperjs';
import { MaterialModule } from '../app.module';

import { DropzoneModule } from 'ngx-dropzone-wrapper';
import { DROPZONE_CONFIG } from 'ngx-dropzone-wrapper';
import { DropzoneConfigInterface } from 'ngx-dropzone-wrapper';

import { LoadingModule } from 'ngx-loading';

import { CanvasWhiteboardModule } from 'ng2-canvas-whiteboard';
import { ColorPickerModule } from 'ngx-color-picker';

const DEFAULT_DROPZONE_CONFIG: DropzoneConfigInterface = {
    // Change this to your upload POST address:
    url: 'https://httpbin.org/post',
    maxFilesize: 50,
    acceptedFiles: null
};

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(PagesRoutes),
    FormsModule,
    MaterialModule,
    ReactiveFormsModule,
    AngularCropperjsModule,
    LoadingModule,
    DropzoneModule,
    CanvasWhiteboardModule,
    ColorPickerModule,
    ResultadoBusquedaModule,
    DocumentViewerModule,
    FolderViewerModule
  ],
  exports:[
    
  ],
  declarations: [
    LoginComponent,
    PortalComponent,
    RegisterComponent,
    PricingComponent,
    LockComponent
  ],
  providers:[
    {
      provide: DROPZONE_CONFIG,
      useValue: DEFAULT_DROPZONE_CONFIG
    }
  ]
})

export class PagesModule {}
