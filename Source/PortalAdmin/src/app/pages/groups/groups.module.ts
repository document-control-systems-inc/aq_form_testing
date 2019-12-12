import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
// import { MdIconModule, MdCardModule, MdInputModule, MdCheckboxModule, MdButtonModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { FlexLayoutModule } from '@angular/flex-layout';

//Routes
import { GroupsRoutes } from './groups.routing';

//Uso de librerías ajenas
import { MaterialModule } from '../../app.module';
import { NgxTreeSelectModule } from 'ngx-tree-select';

//Componentes principales
import { GroupsComponent } from './groups/groups.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(GroupsRoutes),
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    NgxTreeSelectModule.forRoot({
      expandMode: "None"
    })
  ],
  declarations: [
    GroupsComponent
  ],
  schemas: [ NO_ERRORS_SCHEMA ]
})

export class GroupsModule {}
