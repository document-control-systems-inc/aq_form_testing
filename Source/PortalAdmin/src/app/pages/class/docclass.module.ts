import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { FlexLayoutModule } from '@angular/flex-layout';

//Routes
import { DocClassRoutes } from './docclass.routing';

//Uso de librerías ajenas
import { MaterialModule } from '../../app.module';
import { NgxTreeSelectModule } from 'ngx-tree-select';

//Componentes principales
import { DocClassComponent } from './docclass/docclass.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(DocClassRoutes),
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    NgxTreeSelectModule.forRoot({
      expandMode: "None"
    })
  ],
  declarations: [
    DocClassComponent
  ],
  schemas: [ NO_ERRORS_SCHEMA ]
})

export class DocClassModule {}
