import { Routes } from '@angular/router';

import { BusquedaComponent } from './busqueda/busqueda.component';

export const BusquedaRoutes: Routes = [

    {
        path: '',
        children: [ {
            path: '',
            component: BusquedaComponent
        } ]
    }
];
