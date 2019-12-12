import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
// import { MdIconModule, MdCardModule, MdInputModule, MdCheckboxModule, MdButtonModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { FlexLayoutModule } from '@angular/flex-layout';

//Routes
import { TareasRoutes } from './tareas.routing';

//Uso de librerías ajenas
import { MaterialModule } from '../../app.module';
import { DateTimePickerModule } from 'ng-pick-datetime';
import { NgxTreeSelectModule } from 'ngx-tree-select';
import { TreeModule } from 'ng2-tree';

//Componentes principales
import { TareasComponent } from './tareas/tareas.component';
import { PanelTareaComponent } from '../../shared/tareas/panelTarea/panelTarea.component';

//Pipes
import { NgxPaginationModule } from 'ngx-pagination';
import { Ng2SearchPipeModule } from 'ng2-search-filter';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(TareasRoutes),
    FormsModule,
    ReactiveFormsModule,
    DateTimePickerModule,
    MaterialModule,
    TreeModule,
    NgxPaginationModule,
    Ng2SearchPipeModule,
    NgxTreeSelectModule.forRoot({
      expandMode: "None"
    })
  ],
  declarations: [
    TareasComponent,
    PanelTareaComponent
  ],
  schemas: [ NO_ERRORS_SCHEMA ]
})

export class TareasModule {}
