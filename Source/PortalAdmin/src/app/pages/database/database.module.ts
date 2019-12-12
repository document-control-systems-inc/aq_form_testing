import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
// import { MdIconModule, MdCardModule, MdInputModule, MdCheckboxModule, MdButtonModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { FlexLayoutModule } from '@angular/flex-layout';

//Routes
import { DatabaseRoutes } from './database.routing';

//Uso de librerías ajenas
import { MaterialModule } from '../../app.module';

//Componentes principales
import { DatabaseComponent } from './database/database.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(DatabaseRoutes),
    FormsModule,
    ReactiveFormsModule,
    MaterialModule
  ],
  declarations: [
    DatabaseComponent
  ],
  schemas: [ NO_ERRORS_SCHEMA ]
})

export class DatabaseModule {}
