import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
// import { MdIconModule, MdCardModule, MdInputModule, MdCheckboxModule, MdButtonModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { FlexLayoutModule } from '@angular/flex-layout';

//Importación de componentes
import { PdfComponent } from '../../../shared/documentViewer/pdfViewer/pdfViewer.component';
import { PdfEditorComponent } from '../../../shared/documentViewer/pdfEditor/pdfEditor.component';
import { SystemPropertiesComponent } from '../../../shared/documentViewer/systemProperties/systemProperties.component';
import { DocumentPropertiesComponent } from '../../../shared/documentViewer/documentProperties/documentProperties.component';
import { VersionHistoryComponent } from '../../../shared/documentViewer/versionHistory/versionHistory.component';
import { CommentsComponent } from '../../../shared/documentViewer/comments/comments.component';
import { ReactsComponent } from '../../../shared/documentViewer/reacts/reacts.component';
import { TiffViewerComponent } from '../../../shared/documentViewer/tiffViewer/tiffViewer.component';
import { ImageViewerComponent } from '../../../shared/documentViewer/imageViewer/imageViewer.component';
import { VideoPlayerComponent } from '../../../shared/documentViewer/videoPlayer/videoPlayer.component';
import { ImageEditorComponent } from '../../../shared/documentViewer/imageEditor/imageEditor.component';
import { TiffEditorComponent } from '../../../shared/documentViewer/tiffEditor/tiffEditor.component';
import { DocumentViewerComponent } from './documentViewer.component';

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
    LoadingModule
  ],
  exports: [
  	PdfComponent,
    PdfEditorComponent,
    SystemPropertiesComponent,
    DocumentPropertiesComponent,
    VersionHistoryComponent,
    CommentsComponent,
    ReactsComponent,
    PdfViewerComponent,
    TiffViewerComponent,
    ImageViewerComponent,
    VideoPlayerComponent,
    DocumentViewerComponent,
    ImageEditorComponent,
    TiffEditorComponent
  ],
  declarations: [
    PdfComponent,
    PdfEditorComponent,
    SystemPropertiesComponent,
    DocumentPropertiesComponent,
    VersionHistoryComponent,
    CommentsComponent,
    ReactsComponent,
    PdfViewerComponent,
    TiffViewerComponent,
    ImageViewerComponent,
    VideoPlayerComponent,
    DocumentViewerComponent,
    ImageEditorComponent,
    TiffEditorComponent
  ],
  providers: [
    {
      provide: DROPZONE_CONFIG,
      useValue: DEFAULT_DROPZONE_CONFIG
    }
  ],
  schemas: [ NO_ERRORS_SCHEMA ]
})

export class DocumentViewerModule {}
