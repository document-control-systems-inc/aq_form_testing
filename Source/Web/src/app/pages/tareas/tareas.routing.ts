import { Routes } from '@angular/router';

import { TareasComponent } from './tareas/tareas.component';

export const TareasRoutes: Routes = [

    {
        path: '',
        children: [ {
            path: '',
            component: TareasComponent
        } ]
    }
];