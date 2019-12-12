import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
// import { MdIconModule, MdCardModule, MdInputModule, MdCheckboxModule, MdButtonModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { FlexLayoutModule } from '@angular/flex-layout';

//Importación de componentes
import { FolderViewerComponent } from './folderViewer.component';

//Pipes
import { NgxPaginationModule } from 'ngx-pagination';
import { Ng2SearchPipeModule } from 'ng2-search-filter';
import { Ng2OrderModule } from 'ng2-order-pipe';

//Uso de librerías ajenas
import { PdfViewerComponent } from 'ng2-pdf-viewer';
import { MaterialModule } from '../../../app.module';
import { DataTablesModule } from 'angular-datatables';
import { DateTimePickerModule } from 'ng-pick-datetime';
import { LoadingModule } from 'ngx-loading';
import { NgxTreeSelectModule } from 'ngx-tree-select';

import { DropzoneModule } from 'ngx-dropzone-wrapper';
import { DROPZONE_CONFIG } from 'ngx-dropzone-wrapper';
import { DropzoneConfigInterface } from 'ngx-dropzone-wrapper';

//Libreria de arbol ajena
import { TreeModule } from 'ng2-tree';

import 'hammerjs';
import { ImageViewerModule } from '@hallysonh/ngx-imageviewer';
import { VgCoreModule } from 'videogular2/core';
import { VgControlsModule } from 'videogular2/controls';
import { VgOverlayPlayModule } from 'videogular2/overlay-play';
import { VgBufferingModule } from 'videogular2/buffering';

const DEFAULT_DROPZONE_CONFIG: DropzoneConfigInterface = {
    // Change this to your upload POST address:
    url: 'https://httpbin.org/post',
    maxFilesize: 50,
    acceptedFiles: null
};

@NgModule({
  imports: [
    CommonModule,
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
    LoadingModule,
    NgxTreeSelectModule.forRoot({
      expandMode: "None"
    })
  ],
  exports: [
    FolderViewerComponent
  ],
  declarations: [
    FolderViewerComponent
  ],
  providers: [
    {
      provide: DROPZONE_CONFIG,
      useValue: DEFAULT_DROPZONE_CONFIG
    }
  ],
  schemas: [ NO_ERRORS_SCHEMA ]
})

export class FolderViewerModule {}
