import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
// import { MdIconModule, MdCardModule, MdInputModule, MdCheckboxModule, MdButtonModule } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { FlexLayoutModule } from '@angular/flex-layout';

//Importación de componentes
import { CriteriosBusquedaComponent } from '../../shared/busqueda/criteriosBusqueda/criteriosBusqueda.component';
import { TipoBusquedaComponent } from '../../shared/busqueda/tipoBusqueda/tipoBusqueda.component';
import { VisualizacionResultadosComponent } from '../../shared/busqueda/visualizacionResultados/visualizacionResultados.component';
import { NuevaBusquedaComponent } from '../../shared/busqueda/nuevaBusqueda/nuevaBusqueda.component';
import { BusquedaAlmacenadaComponent } from '../../shared/busqueda/busquedaAlmacenada/busquedaAlmacenada.component';
//import { ResultadoBusquedaComponent } from '../../shared/busqueda/resultadoBusqueda/resultadoBusqueda.component';
//import { FolderViewerComponent } from '../../shared/busqueda/folderViewer/folderViewer.component';
import { ResultadoBusquedaModule } from '../../shared/busqueda/resultadoBusqueda/resultadoBusqueda.module';
import { FolderViewerModule } from '../../shared/busqueda/folderViewer/folderViewer.module';

//Routes
import { BusquedaRoutes } from './busqueda.routing';

//Pipes
import { NgxPaginationModule } from 'ngx-pagination';
import { Ng2SearchPipeModule } from 'ng2-search-filter';
import { Ng2OrderModule } from 'ng2-order-pipe';

//Uso de librerías ajenas
import { MaterialModule } from '../../app.module';
import { DateTimePickerModule } from 'ng-pick-datetime';
import { NgxTreeSelectModule } from 'ngx-tree-select';
import { LoadingModule } from 'ngx-loading';

//Componentes principales
import { BusquedaComponent } from './busqueda/busqueda.component';

import { DocumentViewerModule } from '../explorar/documentViewer/documentViewer.module';

@NgModule({
  imports: [
    CommonModule,
    NgxPaginationModule,
    Ng2SearchPipeModule,
    RouterModule.forChild(BusquedaRoutes),
    FormsModule,
    ReactiveFormsModule,
    DateTimePickerModule,
    MaterialModule,
    DocumentViewerModule,
    LoadingModule,
    ResultadoBusquedaModule,
    FolderViewerModule,
    NgxTreeSelectModule.forRoot({
      expandMode: "None"
    })
  ],
  declarations: [
    BusquedaComponent, 
    CriteriosBusquedaComponent,
    TipoBusquedaComponent,
    VisualizacionResultadosComponent,
    NuevaBusquedaComponent,
    BusquedaAlmacenadaComponent,
    //ResultadoBusquedaComponent,
    //FolderViewerComponent
  ],
  schemas: [ NO_ERRORS_SCHEMA ]
})

export class BusquedaModule {}
