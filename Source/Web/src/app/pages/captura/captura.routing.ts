import { Routes } from '@angular/router';

import { CapturaComponent } from './captura/captura.component';

export const CapturaRoutes: Routes = [

    {
        path: '',
        children: [ {
            path: '',
            component: CapturaComponent
        } ]
    }
];