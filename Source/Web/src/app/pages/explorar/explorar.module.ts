import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
// import { MdIconModule, MdCardModule, MdInputModule, MdCheckboxModule, MdButtonModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { FlexLayoutModule } from '@angular/flex-layout';

//Pipes
import { NgxPaginationModule } from 'ngx-pagination';
import { Ng2SearchPipeModule } from 'ng2-search-filter';
import { Ng2OrderModule } from 'ng2-order-pipe';

//Routes
import { ExplorarRoutes } from './explorar.routing';

//Uso de librerías ajenas
import { MaterialModule } from '../../app.module';
import { DataTablesModule } from 'angular-datatables';
import { DateTimePickerModule } from 'ng-pick-datetime';
import { LoadingModule } from 'ngx-loading';
import { NgxTreeSelectModule } from 'ngx-tree-select';

import { DropzoneModule } from 'ngx-dropzone-wrapper';
import { DROPZONE_CONFIG } from 'ngx-dropzone-wrapper';
import { DropzoneConfigInterface } from 'ngx-dropzone-wrapper';

//Componentes principales
import { ExplorarComponent } from './explorar/explorar.component';

//Libreria de arbol ajena
import { TreeModule } from 'ng2-tree';

import 'hammerjs';
import { ImageViewerModule } from '@hallysonh/ngx-imageviewer';
import { VgCoreModule } from 'videogular2/core';
import { VgControlsModule } from 'videogular2/controls';
import { VgOverlayPlayModule } from 'videogular2/overlay-play';
import { VgBufferingModule } from 'videogular2/buffering';

import { DocumentViewerModule } from './documentViewer/documentViewer.module';

const DEFAULT_DROPZONE_CONFIG: DropzoneConfigInterface = {
    // Change this to your upload POST address:
    url: 'http://ec2-52-23-226-55.compute-1.amazonaws.com:8080/aquarius/',
    maxFilesize: 50,
    acceptedFiles: null
};

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(ExplorarRoutes),
    FormsModule,
    ReactiveFormsModule,
    TreeModule,
    MaterialModule,
    DateTimePickerModule,
    DataTablesModule,
    NgxPaginationModule,
    Ng2SearchPipeModule,
    Ng2OrderModule,
    ImageViewerModule,
    VgCoreModule,
    VgControlsModule,
    VgOverlayPlayModule,
    DropzoneModule,
    VgBufferingModule,
    DocumentViewerModule,
    LoadingModule,
    NgxTreeSelectModule.forRoot({
      expandMode: "None"
    })
  ],
  declarations: [
    ExplorarComponent
  ],
  providers: [
    {
      provide: DROPZONE_CONFIG,
      useValue: DEFAULT_DROPZONE_CONFIG
    }
  ],
  schemas: [ NO_ERRORS_SCHEMA ]
})

export class ExplorarModule {}
