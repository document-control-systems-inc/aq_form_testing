import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
// import { MdIconModule, MdCardModule, MdInputModule, MdCheckboxModule, MdButtonModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { FlexLayoutModule } from '@angular/flex-layout';

//Routes
import { ProcessRoutes } from './process.routing';

//Uso de librerías ajenas
import { MaterialModule } from '../../app.module';
import { NgxTreeSelectModule } from 'ngx-tree-select';

import { DropzoneModule } from 'ngx-dropzone-wrapper';
import { DROPZONE_CONFIG } from 'ngx-dropzone-wrapper';
import { DropzoneConfigInterface } from 'ngx-dropzone-wrapper';

//Componentes principales
import { ProcessComponent } from './process/process.component';

const DEFAULT_DROPZONE_CONFIG: DropzoneConfigInterface = {
    // Change this to your upload POST address:
    url: 'https://httpbin.org/post',
    maxFilesize: 50,
    acceptedFiles: 'text/xml'
};

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(ProcessRoutes),
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    DropzoneModule,
    NgxTreeSelectModule.forRoot({
      expandMode: "None"
    })
  ],
  providers: [
    {
      provide: DROPZONE_CONFIG,
      useValue: DEFAULT_DROPZONE_CONFIG
    }
  ],
  declarations: [
    ProcessComponent
  ],
  schemas: [ NO_ERRORS_SCHEMA ]
})

export class ProcessModule {}
