import { Routes } from '@angular/router';

import { PermisosComponent } from './permisos/permisos.component';

export const PermisosRoutes: Routes = [
    {
        path: '',
        children: [ {
            path: '',
            component: PermisosComponent
        } ]
    }
];
