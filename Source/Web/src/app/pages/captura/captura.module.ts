import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
// import { MdIconModule, MdCardModule, MdInputModule, MdCheckboxModule, MdButtonModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { FlexLayoutModule } from '@angular/flex-layout';

//Routes
import { CapturaRoutes } from './captura.routing';

//Uso de librerías ajenas
import { MaterialModule } from '../../app.module';
import { DateTimePickerModule } from 'ng-pick-datetime';
import { NgxTreeSelectModule } from 'ngx-tree-select';
import { TreeModule } from 'ng2-tree';

//Componentes principales
import { CapturaComponent } from './captura/captura.component';

//Pipes
import { NgxPaginationModule } from 'ngx-pagination';
import { Ng2SearchPipeModule } from 'ng2-search-filter';

import { LoadingModule } from 'ngx-loading';

import { WebCamModule } from 'ack-angular-webcam';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(CapturaRoutes),
    FormsModule,
    ReactiveFormsModule,
    DateTimePickerModule,
    MaterialModule,
    TreeModule,
    NgxPaginationModule,
    Ng2SearchPipeModule,
    WebCamModule,
    LoadingModule,
    NgxTreeSelectModule.forRoot({
      expandMode: "None"
    })
  ],
  declarations: [
    CapturaComponent
  ],
  schemas: [ NO_ERRORS_SCHEMA ]
})

export class CapturaModule {}
